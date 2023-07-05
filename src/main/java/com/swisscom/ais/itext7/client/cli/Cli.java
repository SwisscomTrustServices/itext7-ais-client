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
package com.swisscom.ais.itext7.client.cli;

import com.swisscom.ais.itext7.client.AisClient;
import com.swisscom.ais.itext7.client.ETSIAisClient;
import com.swisscom.ais.itext7.client.common.AisClientException;
import com.swisscom.ais.itext7.client.common.Loggers;
import com.swisscom.ais.itext7.client.config.AisClientConfiguration;
import com.swisscom.ais.itext7.client.config.LogbackConfiguration;
import com.swisscom.ais.itext7.client.impl.AisClientImpl;
import com.swisscom.ais.itext7.client.impl.ETSIAisClientImpl;
import com.swisscom.ais.itext7.client.impl.PdfDocumentHandler;
import com.swisscom.ais.itext7.client.model.*;
import com.swisscom.ais.itext7.client.rest.*;
import com.swisscom.ais.itext7.client.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class Cli {

    public static final String SEPARATOR = "--------------------------------------------------------------------------------";
    private static final Logger clientLogger = LoggerFactory.getLogger(Loggers.CLIENT);

    public static void main(String[] args) {
        ClientVersionProvider versionProvider = new ClientVersionProvider();
        versionProvider.initialize();

        ArgumentsService argumentsService = new ArgumentsService(versionProvider);
        try {
            Optional<ArgumentsContext> argumentsContext = argumentsService.parseArguments(args, new File(StringUtils.EMPTY).getAbsolutePath());
            if (argumentsContext.isPresent()) {
                ArgumentsContext context = argumentsContext.get();
                new LogbackConfiguration().initialize(context.getVerboseLevel());
                printStartupSummary(versionProvider, context);
                signDocuments(context);
            }
        } catch (Exception e) {
            System.out.println("Error encountered. Use -help argument to see the detailed options.");
            if (argumentsService.isVerboseLevelActive()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

    private static void printStartupSummary(ClientVersionProvider versionProvider, ArgumentsContext context) {
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

    private static void signDocuments(ArgumentsContext context) throws IOException {
        Properties configProperties = PropertyUtils.loadPropertiesFromFile(context.getConfigFile());
        if (context.getSignature().equals(SignatureMode.ETSI)) {
            signWithETSI(configProperties, context);
        } else {
            signDss(context, configProperties);
        }
    }

    private static void signWithETSI(Properties configProperties, ArgumentsContext context) throws IOException {
        if (context.getInputFiles().size() > 1) {
            clientLogger.error("Only one file signing is supported for ETSI interface.");
            return;
        }
        String inputFile = context.getInputFiles().get(0);
        PdfMetadata pdfMetadata = buildPdfMetadata(inputFile, retrieveOutputFileName(inputFile, context));
        ETSIUserData userData = new ETSIUserData()
                .fromProperties(configProperties)
                .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
                .build();
        Trace trace = new Trace(userData.getTransactionId());
        PdfDocumentHandler pdfDocumentHandler = DocumentUtils.prepareOneDocumentForSigning(pdfMetadata, SignatureMode.ETSI, SignatureType.CMS, userData, trace);

        new LogbackConfiguration().initialize(VerboseLevel.BASIC);
        RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(configProperties).build();
        ETSIRestClient restClient = new ETSIRestClientImpl().withConfiguration(restConfig);


        RestClientConfiguration etsiMtlsRestConfig = new RestClientConfiguration().
                etsiFromConfigurationProvider(configProperties)
                .build();

        String jwtToken = AuthenticationUtils.getJwtToken(configProperties, trace, pdfDocumentHandler, etsiMtlsRestConfig);

        try (ETSIAisClient client = new ETSIAisClientImpl(restClient, configProperties.getProperty("license.file"))) {
            client.signOnDemandWithETSI(pdfDocumentHandler, userData, trace, jwtToken);
        }
    }

    private static void signDss(ArgumentsContext context, Properties configProperties) throws IOException {
        AisClientConfiguration aisConfig = new AisClientConfiguration().fromProperties(configProperties).build();
        RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(configProperties).build();
        SignatureRestClient restClient = new SignatureRestClientImpl().withConfiguration(restConfig);

        try (AisClient client = new AisClientImpl(aisConfig, restClient)) {
            ConsentUrlCallback consentUrlCallback = ((consentUrl, data) -> {
                System.out.println(SEPARATOR);
                System.out.println("Consent URL for declaration of will available here: " + consentUrl);
                System.out.println(SEPARATOR);
            });
            UserData userData = new UserData()
                    .fromProperties(configProperties)
                    .withConsentUrlCallback(consentUrlCallback)
                    .build();
            List<PdfMetadata> pdfsMetadata = context.getInputFiles().stream()
                    .map(inputFilePath -> buildPdfMetadata(inputFilePath, retrieveOutputFileName(inputFilePath, context)))
                    .collect(Collectors.toList());

            clientLogger.info("Start performing the signings for the input file(s). You can trace the corresponding details using the {} trace id.",
                    userData.getTransactionId());
            SignatureResult signatureResult = ClientUtils.sign(client, pdfsMetadata, context.getSignature(), userData);
            clientLogger.info("Signature(s) final result: {} - {}", signatureResult, userData.getTransactionId());
        }
    }

    private static PdfMetadata buildPdfMetadata(String inputFilePath, String outputFilePath) {
        try {
            return new PdfMetadata(new FileInputStream(inputFilePath), new FileOutputStream(outputFilePath), DigestAlgorithm.SHA256);
        } catch (IOException e) {
            throw new AisClientException(String.format("Could not prepare the IO resources for the PDF. Input file path: %s, output file path: %s.",
                    inputFilePath, outputFilePath));
        }
    }

    private static String retrieveOutputFileName(String inputFile, ArgumentsContext context) {
        return Objects.nonNull(context.getOutputFile()) ? context.getOutputFile() : FileUtils.generateOutputFileName(inputFile, context.getSuffix());
    }
}
