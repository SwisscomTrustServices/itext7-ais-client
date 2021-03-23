package com.swisscom.ais.itext.client.service;

import com.swisscom.ais.itext.client.AisClient;
import com.swisscom.ais.itext.client.config.AisClientConfiguration;
import com.swisscom.ais.itext.client.config.LogbackConfiguration;
import com.swisscom.ais.itext.client.impl.AisClientImpl;
import com.swisscom.ais.itext.client.impl.ClientVersionProvider;
import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.model.PdfMetadata;
import com.swisscom.ais.itext.client.model.SignatureMode;
import com.swisscom.ais.itext.client.model.SignatureResult;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.model.VerboseLevel;
import com.swisscom.ais.itext.client.rest.SignatureRestClient;
import com.swisscom.ais.itext.client.rest.SignatureRestClientImpl;
import com.swisscom.ais.itext.client.rest.config.RestClientConfiguration;
import com.swisscom.ais.itext.client.utils.PropertyUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class CliService {

    private static final String SEPARATOR = "--------------------------------------------------------------------------------";
    private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final String TIME_PLACEHOLDER = "#time";

    private final AisRequestService requestService;

    private ClientVersionProvider versionProvider;
    private ArgumentsService argumentsService;
    private AisClientConfiguration aisConfig;
    private SignatureRestClient restClient;
    private UserData userData;

    public CliService() {
        this.requestService = new AisRequestService();
    }

    public CliService(AisRequestService requestService) {
        this.requestService = requestService;
    }

    public Optional<ArgumentsContext> buildArgumentsContext(String[] args) {
        versionProvider = new ClientVersionProvider();
        versionProvider.init();

        argumentsService = new ArgumentsService(versionProvider);
        return argumentsService.parseArguments(args, new File(StringUtils.EMPTY).getAbsolutePath());
    }

    public boolean isVerboseLevelActive() {
        return argumentsService.isVerboseLevelActive();
    }

    public void prepareForSignings(ArgumentsContext context) {
        new LogbackConfiguration().init(context.getVerboseLevel());
        printStartupSummary(versionProvider, context);

        Properties properties = PropertyUtils.loadPropertiesFromFile(context.getConfigFile());

        RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(properties);

        restClient = new SignatureRestClientImpl().withConfiguration(restConfig);

        aisConfig = new AisClientConfiguration().fromProperties(properties);

        userData = new UserData()
            .fromProperties(properties)
            .withConsentUrlCallback(((consentUrl, data) -> {
                System.out.println(SEPARATOR);
                System.out.println("Consent URL for declaration of will available here: " + consentUrl);
                System.out.println(SEPARATOR);
            }));
    }

    public void performSignings(ArgumentsContext context) {
        List<PdfMetadata> pdfsMetadata = context.getInputFiles().stream()
            .map(inputFilePath -> new PdfMetadata(inputFilePath, retrieveOutputFileName(inputFilePath, context)))
            .collect(Collectors.toList());

        try (AisClient client = new AisClientImpl(requestService, aisConfig, restClient, context.getVerboseLevel())) {
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
        System.out.println(SEPARATOR);
        System.out.println("Signature final result: " + signatureResult);
        System.out.println(SEPARATOR);
    }

    private void printStartupSummary(ClientVersionProvider versionProvider, ArgumentsContext argumentsContext) {
        System.out.println(SEPARATOR);
        if (versionProvider.isVersionInfoAvailable()) {
            System.out.println("Swisscom AIS Client - " + versionProvider.getVersionInfo());
        } else {
            System.out.println("Swisscom AIS Client");
        }
        System.out.println(SEPARATOR);
        System.out.println("Starting with following parameters:");
        System.out.println("Config            : " + argumentsContext.getConfigFile());
        System.out.println("Input file(s)     : " + String.join(", ", argumentsContext.getInputFiles()));
        System.out.println("Output file       : " + argumentsContext.getOutputFile());
        System.out.println("Suffix            : " + argumentsContext.getSuffix());
        System.out.println("Type of signature : " + argumentsContext.getSignature());
        System.out.println("Verbose level     : " + argumentsContext.getVerboseLevel());
        System.out.println(SEPARATOR);
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
