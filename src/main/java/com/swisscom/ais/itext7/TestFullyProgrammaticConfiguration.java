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
import com.swisscom.ais.itext7.client.model.DigestAlgorithm;
import com.swisscom.ais.itext7.client.model.PdfMetadata;
import com.swisscom.ais.itext7.client.model.SignatureResult;
import com.swisscom.ais.itext7.client.model.SignatureStandard;
import com.swisscom.ais.itext7.client.model.UserData;
import com.swisscom.ais.itext7.client.rest.SignatureRestClient;
import com.swisscom.ais.itext7.client.rest.SignatureRestClientImpl;
import com.swisscom.ais.itext7.client.rest.RestClientConfiguration;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

/**
 * Test that shows how to configure the REST and AIS clients from the code. This can also be switched to configuration via the Spring framework or
 * other similar DI frameworks.
 */
public class TestFullyProgrammaticConfiguration {

    public static void main(String[] args) throws IOException {
        // configuration for the REST client; this is done once per application lifetime
        RestClientConfiguration restConfig = RestClientConfiguration.builder()
            .withServiceSignUrl("https://ais.swisscom.com/AIS-Server/rs/v1.0/sign")
            .withServicePendingUrl("https://ais.swisscom.com/AIS-Server/rs/v1.0/pending")
            // the server certificate file is optional, in case it is omitted the CA must be a trusted one
            .withServerCertificateFile("/home/user/ais-server.crt")
            .withClientKeyFile("/home/user/ais-client.key")
            .withClientKeyPassword("secret")
            .withClientCertificateFile("/home/user/ais-client.crt")
            .build();

        SignatureRestClient restClient = new SignatureRestClientImpl().withConfiguration(restConfig);

        // then configure the AIS client; this is done once per application lifetime
        AisClientConfiguration aisConfig = new AisClientConfiguration(10, 10, "${ITEXT_LICENSE_FILE_PATH}");

        try (AisClient aisClient = new AisClientImpl(aisConfig, restClient)) {
            // third, configure a UserData instance with details about this signature
            // this is done for each signature (can also be created once and cached on a per-user basis)
            UserData userData = UserData.builder()
                .withClaimedIdentityName("ais-90days-trial")
                .withClaimedIdentityKey("keyEntity")
                .withDistinguishedName("cn=TEST User, givenname=Max, surname=Maximus, c=US, serialnumber=abcdefabcdefabcdefabcdefabcdef")
                .withStepUpLanguage("en")
                .withStepUpMessage("Please confirm the signing of the document")
                .withStepUpMsisdn("40799999999")
                .withSignatureReason("For testing purposes")
                .withSignatureLocation("Topeka, Kansas")
                .withSignatureContactInfo("test@test.com")
                .withSignatureStandard(SignatureStandard.PDF)
                .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
                .build();

            // fourth, populate a PdfHandle with details about the document to be signed. More than one PdfHandle can be given
            PdfMetadata document = new PdfMetadata(new FileInputStream("/home/user/input.pdf"),
                                                   new FileOutputStream("/home/user/signed-output.pdf"), DigestAlgorithm.SHA256);

            SignatureResult signatureResult = aisClient.signWithOnDemandCertificateAndStepUp(Collections.singletonList(document), userData);
            System.out.println("Finish to sign the document(s) with the status: " + signatureResult);
        }
    }
}
