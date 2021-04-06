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
