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
package com.swisscom.ais.itext;

import com.swisscom.ais.itext.client.config.LogbackConfiguration;
import com.swisscom.ais.itext.client.model.ArgumentsContext;
import com.swisscom.ais.itext.client.model.SignatureMode;
import com.swisscom.ais.itext.client.model.SignatureResult;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.model.VerboseLevel;
import com.swisscom.ais.itext.client.service.AisRequestService;
import com.swisscom.ais.itext.client.service.SigningService;

import java.util.Properties;

public class TestStaticSignature {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(TestStaticSignature.class.getResourceAsStream("/local-config.properties"));

        new LogbackConfiguration().initialize(VerboseLevel.BASIC);
        SigningService signingService = new SigningService(new AisRequestService());
        signingService.initialize(properties);

        ArgumentsContext context = ArgumentsContext.builder()
            .withInputFile(properties.getProperty("local.test.inputFile"))
            .withOutputFile(properties.getProperty("local.test.outputFilePrefix") + System.currentTimeMillis() + ".pdf")
            .withSignature(SignatureMode.STATIC)
            .build();
        UserData userData = new UserData()
            .fromProperties(properties)
            .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
            .build();
        SignatureResult signatureResult = signingService.performSignings(context, userData);
        System.out.println("Finish to sign the document(s) with the status: " + signatureResult);
        signingService.close();
    }
}
