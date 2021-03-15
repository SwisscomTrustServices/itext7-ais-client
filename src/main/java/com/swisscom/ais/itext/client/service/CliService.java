package com.swisscom.ais.itext.client.service;

import com.swisscom.ais.itext.client.impl.Soap;
import com.swisscom.ais.itext.client.config.AisClientConfiguration;
import com.swisscom.ais.itext.client.config.LogbackConfiguration;
import com.swisscom.ais.itext.client.impl.ClientVersionProvider;
import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.model.VerboseLevel;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class CliService {

    private static final String SEPARATOR = "--------------------------------------------------------------------------------";

    private ClientVersionProvider versionProvider;
    private ArgumentsService argumentsService;
    private Properties properties;
    private AisClientConfiguration aisConfig;

    private static void printStartupSummary(ClientVersionProvider versionProvider, ArgumentsContext argumentsContext) {
        System.out.println(SEPARATOR);
        if (versionProvider.isVersionInfoAvailable()) {
            System.out.println("Swisscom AIS Client - " + versionProvider.getVersionInfo());
        } else {
            System.out.println("Swisscom AIS Client");
        }
        System.out.println(SEPARATOR);
        System.out.println("Starting with following parameters:");
        System.out.println("Config            : " + argumentsContext.getConfigFile());
        System.out.println("Input file(s)     : " + String.join(",", argumentsContext.getInputFiles()));
        System.out.println("Output file       : " + argumentsContext.getOutputFile());
        System.out.println("Suffix            : " + argumentsContext.getSuffix());
        System.out.println("Type of signature : " + argumentsContext.getSignatureType());
        System.out.println("Verbose level     : " + argumentsContext.getVerboseLevel());
        System.out.println(SEPARATOR);
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

    public void prepareSignings(ArgumentsContext context) throws IOException {
        new LogbackConfiguration().init(context.getVerboseLevel());
        printStartupSummary(versionProvider, context);

        properties = new Properties();
        properties.load(new FileReader(context.getConfigFile()));

        // todo rest client config

        aisConfig = new AisClientConfiguration();
        aisConfig.setFromProperties(properties);
    }

    public void performSignings(ArgumentsContext context) throws FileNotFoundException {
        boolean verboseMode = context.getVerboseLevel().equals(VerboseLevel.BASIC);
        boolean debugMode = context.getVerboseLevel().equals(VerboseLevel.MEDIUM);

        Soap dss_soap = new Soap(verboseMode, debugMode, properties, aisConfig);

        try {
            for (String inputFile : context.getInputFiles()) {
                dss_soap.sign(context.getSignatureType(), inputFile, context.getOutputFile(), context.getSignatureReason(),
                              context.getSignatureLocation(), context.getSignatureContactInfo(), context.getCertificationLevel(),
                              context.getDistinguishedName(), context.getStepUpMsisdn(), context.getStepUpMessage(), context.getStepUpLanguage(),
                              context.getStepUpSerialNo());
            }
        } catch (Exception e) {
            if (!context.getVerboseLevel().equals(VerboseLevel.LOW)) {
                e.printStackTrace();
            }
        }
    }
}
