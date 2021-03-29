package com.swisscom.ais.itext.client;

import com.swisscom.ais.itext.client.config.LogbackConfiguration;
import com.swisscom.ais.itext.client.impl.ClientVersionProvider;
import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.model.ConsentUrlCallback;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.service.AisRequestService;
import com.swisscom.ais.itext.client.service.ArgumentsService;
import com.swisscom.ais.itext.client.service.SigningService;
import com.swisscom.ais.itext.client.utils.PropertyUtils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Optional;
import java.util.Properties;

public class SignPdfCli {

    public static void main(String[] args) {
        ClientVersionProvider versionProvider = new ClientVersionProvider();
        versionProvider.initialize();

        ArgumentsService argumentsService = new ArgumentsService(versionProvider);
        try {
            Optional<ArgumentsContext> argumentsContext = argumentsService.parseArguments(args, new File(StringUtils.EMPTY).getAbsolutePath());
            if (argumentsContext.isPresent()) {
                ArgumentsContext context = argumentsContext.get();
                new LogbackConfiguration().initialize(context.getVerboseLevel());
                SigningService signingService = new SigningService(new AisRequestService(), versionProvider);
                signingService.printStartupSummary(context);
                Properties configProperties = PropertyUtils.loadPropertiesFromFile(context.getConfigFile());
                signingService.initialize(configProperties);
                ConsentUrlCallback consentUrlCallback = ((consentUrl, data) -> {
                    System.out.println(SigningService.SEPARATOR);
                    System.out.println("Consent URL for declaration of will available here: " + consentUrl);
                    System.out.println(SigningService.SEPARATOR);
                });
                UserData userData = new UserData()
                    .fromProperties(configProperties)
                    .withConsentUrlCallback(consentUrlCallback)
                    .build();
                signingService.performSignings(context, userData);
            }
        } catch (Exception e) {
            System.out.println("Error encountered. Use -help argument to see the detailed options.");
            if (argumentsService.isVerboseLevelActive()) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }
}
