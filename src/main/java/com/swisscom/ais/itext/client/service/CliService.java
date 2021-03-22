package com.swisscom.ais.itext.client.service;

import com.swisscom.ais.itext.client.impl.Soap;
import com.swisscom.ais.itext.client.config.AisClientConfiguration;
import com.swisscom.ais.itext.client.config.LogbackConfiguration;
import com.swisscom.ais.itext.client.impl.ClientVersionProvider;
import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.model.PdfMetadata;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.model.VerboseLevel;
import com.swisscom.ais.itext.client.rest.SignatureRestClient;
import com.swisscom.ais.itext.client.rest.SignatureRestClientImpl;
import com.swisscom.ais.itext.client.rest.config.RestClientConfiguration;
import com.swisscom.ais.itext.client.utils.PropertyUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
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

    private ClientVersionProvider versionProvider;
    private ArgumentsService argumentsService;
    private AisClientConfiguration aisConfig;
    private Properties properties;
    private SignatureRestClient restClient;
    private UserData userData;

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

        properties = PropertyUtils.loadPropertiesFromFile(context.getConfigFile());

        RestClientConfiguration restConfig = new RestClientConfiguration();
        restConfig.setFromProperties(properties);

        restClient = new SignatureRestClientImpl().setConfiguration(restConfig);

        aisConfig = new AisClientConfiguration();
        aisConfig.setFromProperties(properties);

        userData = new UserData();
        userData.setFromProperties(properties);
        userData.setConsentUrlCallback(((consentUrl, data) -> {
            System.out.println(SEPARATOR);
            System.out.println("Consent URL for declaration of will available here: " + consentUrl);
            System.out.println(SEPARATOR);
        }));
    }

    public void performSignings(ArgumentsContext context) throws FileNotFoundException {
        // todo use this
        List<PdfMetadata> pdfsMetadata = context.getInputFiles().stream()
            .map(inputFilePath -> new PdfMetadata(inputFilePath, retrieveOutputFileName(inputFilePath, context)))
            .collect(Collectors.toList());

        boolean verboseMode = context.getVerboseLevel().equals(VerboseLevel.BASIC);
        boolean debugMode = context.getVerboseLevel().equals(VerboseLevel.MEDIUM);

        Soap dss_soap = new Soap(verboseMode, debugMode, properties, aisConfig);

        try {
            for (PdfMetadata metadata : pdfsMetadata) {
                dss_soap.sign(context.getSignature(), metadata, userData);
            }
        } catch (Exception e) {
            if (!context.getVerboseLevel().equals(VerboseLevel.LOW)) {
                e.printStackTrace();
            }
        }
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
