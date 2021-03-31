package com.swisscom.ais.itext.client.service;

import com.swisscom.ais.itext.client.AisClient;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

public class SigningService {

    public static final String SEPARATOR = "--------------------------------------------------------------------------------";
    private static final Logger clientLogger = LoggerFactory.getLogger(Loggers.CLIENT);
    private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String TIME_PLACEHOLDER = "#time";

    private final AisRequestService requestService;

    private ClientVersionProvider versionProvider;
    private AisClientConfiguration aisConfig;
    private SignatureRestClient restClient;

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
        aisConfig = new AisClientConfiguration().fromProperties(properties);
        RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(properties).build();
        restClient = new SignatureRestClientImpl().withConfiguration(restConfig);
    }

    public void initialize(AisClientConfiguration builtAisConfig, SignatureRestClient builtRestClient) {
        aisConfig = builtAisConfig;
        restClient = builtRestClient;
    }

    public SignatureResult performSignings(ArgumentsContext context, UserData userData) throws Exception {
        List<PdfMetadata> pdfsMetadata = context.getInputFiles().stream()
            .map(inputFilePath -> new PdfMetadata(inputFilePath, retrieveOutputFileName(inputFilePath, context)))
            .collect(Collectors.toList());

        return performSignings(pdfsMetadata, context.getSignature(), userData);
    }

    public SignatureResult performSignings(List<PdfMetadata> pdfsMetadata, SignatureMode signatureMode, UserData userData) throws Exception {
        clientLogger.info("Start performing the signings for the input file(s): {}. You can trace the corresponding details using the {} trace id.",
                          pdfsMetadata.stream().map(PdfMetadata::getInputFilePath).collect(Collectors.joining(", ")), userData.getTransactionId());
        try (AisClient client = new AisClientImpl(requestService, aisConfig, restClient)) {
            SignatureResult signatureResult = sign(client, pdfsMetadata, signatureMode, userData);
            logResultInfo(signatureResult);
            return signatureResult;
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
