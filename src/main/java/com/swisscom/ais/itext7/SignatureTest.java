package com.swisscom.ais.itext7;

import com.swisscom.ais.itext7.client.AisClient;
import com.swisscom.ais.itext7.client.config.AisClientConfiguration;
import com.swisscom.ais.itext7.client.config.LogbackConfiguration;
import com.swisscom.ais.itext7.client.impl.AisClientImpl;
import com.swisscom.ais.itext7.client.model.PdfMetadata;
import com.swisscom.ais.itext7.client.model.SignatureMode;
import com.swisscom.ais.itext7.client.model.SignatureResult;
import com.swisscom.ais.itext7.client.model.UserData;
import com.swisscom.ais.itext7.client.model.VerboseLevel;
import com.swisscom.ais.itext7.client.rest.SignatureRestClient;
import com.swisscom.ais.itext7.client.rest.SignatureRestClientImpl;
import com.swisscom.ais.itext7.client.rest.RestClientConfiguration;
import com.swisscom.ais.itext7.client.utils.ClientUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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

        try (AisClient client = new AisClientImpl(aisConfig, restClient)) {
            String inputFilePath = configProperties.getProperty("local.test.inputFile");
            String outputFilePath = configProperties.getProperty("local.test.outputFilePrefix") + System.currentTimeMillis() + ".pdf";
            PdfMetadata document = new PdfMetadata(new FileInputStream(inputFilePath), new FileOutputStream(outputFilePath));
            List<PdfMetadata> pdfsMetadata = Collections.singletonList(document);
            UserData userData = new UserData()
                .fromProperties(configProperties)
                .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
                .build();
            SignatureResult signatureResult = ClientUtils.sign(client, pdfsMetadata, signatureMode, userData);
            System.out.println("Finish to sign the document(s) with the status: " + signatureResult);
        }
    }
}
