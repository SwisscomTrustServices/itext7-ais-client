package com.swisscom.ais.itext;

import com.swisscom.ais.itext.client.config.AisClientConfiguration;
import com.swisscom.ais.itext.client.impl.AisClientImpl;
import com.swisscom.ais.itext.client.model.PdfMetadata;
import com.swisscom.ais.itext.client.model.SignatureResult;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.rest.SignatureRestClient;
import com.swisscom.ais.itext.client.rest.SignatureRestClientImpl;
import com.swisscom.ais.itext.client.rest.config.RestClientConfiguration;
import com.swisscom.ais.itext.client.service.AisRequestService;

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
        AisClientConfiguration aisConfig = new AisClientConfiguration().fromProperties(properties);

        try (AisClientImpl aisClient = new AisClientImpl(new AisRequestService(), aisConfig, restClient)) {
            // third, load even the user data from the properties store
            UserData userData = new UserData()
                .fromProperties(properties)
                .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
                .build();

            // finally, use the properties store even for local testing data
            PdfMetadata document = new PdfMetadata();
            document.setInputFilePath(properties.getProperty("local.test.inputFile"));
            document.setOutputFilePath(properties.getProperty("local.test.outputFilePrefix") + System.currentTimeMillis() + ".pdf");

            SignatureResult result = aisClient.signWithOnDemandCertificateAndStepUp(Collections.singletonList(document), userData);
            System.out.println("Finish to sign the document(s) with the status: " + result);
        }
    }
}