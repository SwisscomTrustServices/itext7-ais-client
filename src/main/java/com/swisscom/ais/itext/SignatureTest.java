package com.swisscom.ais.itext;

import com.swisscom.ais.itext.client.AisClient;
import com.swisscom.ais.itext.client.config.AisClientConfiguration;
import com.swisscom.ais.itext.client.config.LogbackConfiguration;
import com.swisscom.ais.itext.client.impl.AisClientImpl;
import com.swisscom.ais.itext.client.model.PdfMetadata;
import com.swisscom.ais.itext.client.model.SignatureMode;
import com.swisscom.ais.itext.client.model.SignatureResult;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.model.VerboseLevel;
import com.swisscom.ais.itext.client.rest.SignatureRestClient;
import com.swisscom.ais.itext.client.rest.SignatureRestClientImpl;
import com.swisscom.ais.itext.client.rest.config.RestClientConfiguration;
import com.swisscom.ais.itext.client.service.AisRequestService;
import com.swisscom.ais.itext.client.service.SigningService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class SignatureTest {

    public static void sign(Properties configProperties, SignatureMode signatureMode) throws IOException {
        new LogbackConfiguration().initialize(VerboseLevel.BASIC);
        AisClientConfiguration aisConfig = new AisClientConfiguration().fromProperties(configProperties).build();
        RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(configProperties).build();
        SignatureRestClient restClient = new SignatureRestClientImpl().withConfiguration(restConfig);

        try (AisClient client = new AisClientImpl(new AisRequestService(), aisConfig, restClient)) {
            SigningService signingService = new SigningService(client);

            String inputFile = configProperties.getProperty("local.test.inputFile");
            String outputFile = configProperties.getProperty("local.test.outputFilePrefix") + System.currentTimeMillis() + ".pdf";
            List<PdfMetadata> pdfsMetadata = Collections.singletonList(new PdfMetadata(inputFile, outputFile));
            UserData userData = new UserData()
                .fromProperties(configProperties)
                .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
                .build();
            SignatureResult signatureResult = signingService.performSignings(pdfsMetadata, signatureMode, userData);
            System.out.println("Finish to sign the document(s) with the status: " + signatureResult);
        }
    }
}
