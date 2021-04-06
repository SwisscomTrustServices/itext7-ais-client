/*
 * Copyright 2021 Swisscom (Schweiz) AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swisscom.ais.itext.client.service;

import com.itextpdf.licensekey.LicenseKeyException;
import com.swisscom.ais.itext.client.AisClient;
import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.common.Loggers;
import com.swisscom.ais.itext.client.config.AisClientConfiguration;
import com.swisscom.ais.itext.client.impl.AisClientImpl;
import com.swisscom.ais.itext.client.impl.ClientVersionProvider;
import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.model.PdfMetadata;
import com.swisscom.ais.itext.client.model.SignatureMode;
import com.swisscom.ais.itext.client.model.SignatureResult;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.rest.SignatureRestClient;
import com.swisscom.ais.itext.client.rest.SignatureRestClientImpl;
import com.swisscom.ais.itext.client.rest.config.RestClientConfiguration;
import com.swisscom.ais.itext.client.utils.PropertyUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Aim to build an {@link AisClient}, thus hiding the implementation details and providing a more abstract way to sign documents. However, the
 * {@link AisClient} can be directly used, but is not recommended.
 */
public class SigningService implements Closeable {

    public static final String SEPARATOR = "--------------------------------------------------------------------------------";
    private static final Logger clientLogger = LoggerFactory.getLogger(Loggers.CLIENT);
    private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String TIME_PLACEHOLDER = "#time";

    private final AisRequestService requestService;
    private ClientVersionProvider versionProvider;
    private AisClient client;

    public SigningService(AisRequestService requestService) {
        this.requestService = requestService;
    }

    public SigningService(AisRequestService requestService, ClientVersionProvider versionProvider) {
        this.requestService = requestService;
        this.versionProvider = versionProvider;
    }

    public void initialize(String configFilePath) {
        initialize(PropertyUtils.loadPropertiesFromFile(configFilePath));
    }

    public void initialize(Properties properties) {
        AisClientConfiguration aisConfig = new AisClientConfiguration().fromProperties(properties);
        RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(properties).build();
        SignatureRestClient restClient = new SignatureRestClientImpl().withConfiguration(restConfig);
        initialize(aisConfig, restClient);
    }

    public void initialize(AisClientConfiguration builtAisConfig, SignatureRestClient builtRestClient) {
        client = new AisClientImpl(requestService, builtAisConfig, builtRestClient);
    }

    /**
     * @param context  the {@linkplain ArgumentsContext arguments context} which will be considered to retrieve the input documents and the signature
     *                 mode
     * @param userData the {@linkplain UserData user specific metadata} for signing
     * @return the {@linkplain SignatureResult signature result}
     * @throws IllegalArgumentException if the documents can not be signed with the provided signature mode
     * @throws LicenseKeyException      if the iText license was not loaded previously
     * @throws AisClientException       if the signature acquisition process from the AIS service fails
     */
    public SignatureResult performSignings(ArgumentsContext context, UserData userData) {
        List<PdfMetadata> pdfsMetadata = context.getInputFiles().stream()
            .map(inputFilePath -> new PdfMetadata(inputFilePath, retrieveOutputFileName(inputFilePath, context)))
            .collect(Collectors.toList());

        return performSignings(pdfsMetadata, context.getSignature(), userData);
    }

    /**
     * @param pdfsMetadata  the documents metadata to be signed
     * @param signatureMode the {@linkplain SignatureMode signature mode} to be used to sign the PDFs
     * @param userData      the {@linkplain UserData user specific metadata} for signing
     * @return the {@linkplain SignatureResult signature result}
     * @throws IllegalArgumentException if the documents can not be signed with the provided signature mode
     * @throws LicenseKeyException      if the iText license was not loaded previously
     * @throws AisClientException       if the signature acquisition process from the AIS service fails
     */
    public SignatureResult performSignings(List<PdfMetadata> pdfsMetadata, SignatureMode signatureMode, UserData userData) {
        clientLogger.info("Start performing the signings for the input file(s): {}. You can trace the corresponding details using the {} trace id.",
                          pdfsMetadata.stream().map(PdfMetadata::getInputFilePath).collect(Collectors.joining(", ")), userData.getTransactionId());
        SignatureResult signatureResult = sign(client, pdfsMetadata, signatureMode, userData);
        logResultInfo(signatureResult);
        return signatureResult;
    }

    @Override
    public void close() throws IOException {
        if (Objects.nonNull(client)) {
            client.close();
        }
    }

    public void printStartupSummary(ArgumentsContext context) {
        clientLogger.info(SEPARATOR);
        StringBuilder clientInfo = new StringBuilder("Swisscom AIS Client");
        if (Objects.nonNull(versionProvider) && versionProvider.isVersionInfoAvailable()) {
            clientInfo.append(" - ").append(versionProvider.getVersionInfo());
        }
        clientLogger.info(clientInfo.toString());
        clientLogger.info(SEPARATOR);
        clientLogger.info("Starting with the following parameters:");
        clientLogger.info("Config: {}", context.getConfigFile());
        clientLogger.info("Input file(s): {}", String.join(", ", context.getInputFiles()));
        clientLogger.info("Output file: {}", context.getOutputFile());
        clientLogger.info("Suffix: {}", context.getSuffix());
        clientLogger.info("Type of signature: {}", context.getSignature());
        clientLogger.info("Verbose level: {}", context.getVerboseLevel());
        clientLogger.info(SEPARATOR);
    }

    private SignatureResult sign(AisClient client, List<PdfMetadata> pdfsMetadata, SignatureMode signatureMode, UserData userData) {
        switch (signatureMode) {
            case TIMESTAMP:
                return client.signWithTimestamp(pdfsMetadata, userData);
            case STATIC:
                return client.signWithStaticCertificate(pdfsMetadata, userData);
            case ON_DEMAND:
                return client.signWithOnDemandCertificate(pdfsMetadata, userData);
            case ON_DEMAND_WITH_STEP_UP:
                return client.signWithOnDemandCertificateAndStepUp(pdfsMetadata, userData);
            default:
                throw new IllegalArgumentException(String.format("Invalid signature mode. Can not sign the document(s) with the %s signature. - %s",
                                                                 signatureMode, userData.getTransactionId()));
        }
    }

    private void logResultInfo(SignatureResult signatureResult) {
        clientLogger.info(SEPARATOR);
        clientLogger.info("Signature(s) final result: {}", signatureResult);
        clientLogger.info(SEPARATOR);
    }

    private String retrieveOutputFileName(String inputFile, ArgumentsContext context) {
        return Objects.nonNull(context.getOutputFile()) ? context.getOutputFile() : generateOutputFileName(inputFile, context.getSuffix());
    }

    private String generateOutputFileName(String inputFile, String suffix) {
        String finalSuffix = suffix.replaceAll(TIME_PLACEHOLDER, TIME_PATTERN.format(LocalDateTime.now()));
        int extensionIndex = inputFile.lastIndexOf('.');
        return extensionIndex > 0 ? inputFile.substring(0, extensionIndex) + finalSuffix + inputFile.substring(extensionIndex)
                                  : inputFile + finalSuffix;
    }
}
