package com.swisscom.ais.itext.client.service;

import com.swisscom.ais.itext.client.AisClient;
import com.swisscom.ais.itext.client.common.Loggers;
import com.swisscom.ais.itext.client.config.AisClientConfiguration;
import com.swisscom.ais.itext.client.impl.AisClientImpl;
import com.swisscom.ais.itext.client.impl.ClientVersionProvider;
import com.swisscom.ais.itext.client.model.*;
import com.swisscom.ais.itext.client.rest.SignatureRestClient;
import com.swisscom.ais.itext.client.rest.SignatureRestClientImpl;
import com.swisscom.ais.itext.client.rest.config.RestClientConfiguration;
import com.swisscom.ais.itext.client.utils.PropertyUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class SigningService {

    private static final Logger clientLogger = LoggerFactory.getLogger(Loggers.CLIENT);

    public static final String SEPARATOR = "--------------------------------------------------------------------------------";
    private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String TIME_PLACEHOLDER = "#time";

    private final AisRequestService requestService;
    private final ClientVersionProvider versionProvider;

    private AisClientConfiguration aisConfig;
    private SignatureRestClient restClient;
    private UserData userData;

    public SigningService(AisRequestService requestService, ClientVersionProvider versionProvider) {
        this.requestService = requestService;
        this.versionProvider = versionProvider;
    }

    public void prepareForSignings(ArgumentsContext context, ConsentUrlCallback consentUrlCallback) {
        prepareForSignings(context, consentUrlCallback, PropertyUtils.loadPropertiesFromFile(context.getConfigFile()));
    }

    public void prepareForSignings(ArgumentsContext context, ConsentUrlCallback consentUrlCallback, Properties properties) {
        printStartupSummary(versionProvider, context);

        RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(properties);

        restClient = new SignatureRestClientImpl().withConfiguration(restConfig);

        aisConfig = new AisClientConfiguration().fromProperties(properties);

        userData = new UserData().fromProperties(properties).withConsentUrlCallback(consentUrlCallback);
    }

    public void performSignings(ArgumentsContext context) {
        List<PdfMetadata> pdfsMetadata = context.getInputFiles().stream()
            .map(inputFilePath -> new PdfMetadata(inputFilePath, retrieveOutputFileName(inputFilePath, context)))
            .collect(Collectors.toList());

        try (AisClient client = new AisClientImpl(requestService, aisConfig, restClient)) {
            SignatureResult signatureResult = sign(client, pdfsMetadata, context.getSignature());
            logResultInfo(signatureResult);
        } catch (Exception e) {
            if (!context.getVerboseLevel().equals(VerboseLevel.LOW)) {
                e.printStackTrace();
            }
        }
    }

    private SignatureResult sign(AisClient client, List<PdfMetadata> pdfsMetadata, SignatureMode signatureMode) {
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
                throw new IllegalArgumentException(String.format("Invalid type. Can not sign the document(s) with the %s signature.", signatureMode));
        }
    }

    private void logResultInfo(SignatureResult signatureResult) {
        clientLogger.info(SEPARATOR);
        clientLogger.info("Signature final result: {}", signatureResult);
        clientLogger.info(SEPARATOR);
    }

    private void printStartupSummary(ClientVersionProvider versionProvider, ArgumentsContext argumentsContext) {
        clientLogger.info(SEPARATOR);
        StringBuilder clientInfo = new StringBuilder("Swisscom AIS Client");
        if (versionProvider.isVersionInfoAvailable()) {
            clientInfo.append(" - ").append(versionProvider.getVersionInfo());
        }
        clientLogger.info(clientInfo.toString());
        clientLogger.info(SEPARATOR);
        clientLogger.info("Starting with the following parameters:");
        clientLogger.info("Config: {}", argumentsContext.getConfigFile());
        clientLogger.info("Input file(s): {}", String.join(", ", argumentsContext.getInputFiles()));
        clientLogger.info("Output file: {}", argumentsContext.getOutputFile());
        clientLogger.info("Suffix: {}", argumentsContext.getSuffix());
        clientLogger.info("Type of signature: {}", argumentsContext.getSignature());
        clientLogger.info("Verbose level: {}", argumentsContext.getVerboseLevel());
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
