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
package com.swisscom.ais.itext7;

import com.swisscom.ais.itext7.client.AisClient;
import com.swisscom.ais.itext7.client.config.AisClientConfiguration;
import com.swisscom.ais.itext7.client.impl.AisClientImpl;
import com.swisscom.ais.itext7.client.model.PdfMetadata;
import com.swisscom.ais.itext7.client.model.SignatureResult;
import com.swisscom.ais.itext7.client.model.UserData;
import com.swisscom.ais.itext7.client.rest.SignatureRestClient;
import com.swisscom.ais.itext7.client.rest.SignatureRestClientImpl;
import com.swisscom.ais.itext7.client.rest.RestClientConfiguration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Properties;

/**
 * Test with an On Demand signature with Step Up that shows how to access all the configuration available and load it from a
 * properties file. The same configuration can also be tweaked by hand or via some framework (e.g. Spring, Guice, etc).
 */
public class TestAllConfigurable {

    public static void main(String[] args) throws Exception {
        // first load the properties from a local file
        Properties properties = new Properties();
        properties.load(TestOnDemandSignature.class.getResourceAsStream("/local-config.properties"));

        // load the REST client config
        RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(properties).build();

        SignatureRestClient restClient = new SignatureRestClientImpl().withConfiguration(restConfig);

        // load the AIS client config
        AisClientConfiguration aisConfig = new AisClientConfiguration().fromProperties(properties).build();

        try (AisClient aisClient = new AisClientImpl(aisConfig, restClient)) {
            // third, load even the user data from the properties store
            UserData userData = new UserData()
                .fromProperties(properties)
                .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
                .build();

            // finally, use the properties store even for local testing data
            String inputFilePath = properties.getProperty("local.test.inputFile");
            String outputFilePath = properties.getProperty("local.test.outputFilePrefix") + System.currentTimeMillis() + ".pdf";
            PdfMetadata document = new PdfMetadata(new FileInputStream(inputFilePath), new FileOutputStream(outputFilePath));

            SignatureResult result = aisClient.signWithOnDemandCertificateAndStepUp(Collections.singletonList(document), userData);
            System.out.println("Finish to sign the document(s) with the status: " + result);
        }
    }
}
