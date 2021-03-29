package com.swisscom.ais.itext;

import com.swisscom.ais.itext.client.model.PdfMetadata;
import com.swisscom.ais.itext.client.model.SignatureMode;
import com.swisscom.ais.itext.client.model.SignatureResult;
import com.swisscom.ais.itext.client.model.UserData;
import com.swisscom.ais.itext.client.service.AisRequestService;
import com.swisscom.ais.itext.client.service.SigningService;

import java.util.Collections;
import java.util.Properties;

/**
 * Test with an On Demand signature with Step Up that shows how to access all the configuration available and load it from a
 * properties file using a dedicate signing service. The same configuration can also be tweaked by hand or via some framework
 * (e.g. Spring, Guice, etc).
 */
public class TestAllConfigurableSecond {

    public static void main(String[] args) throws Exception {
        // first load the properties from a local file
        Properties properties = new Properties();
        properties.load(TestOnDemandSignature.class.getResourceAsStream("/local-config.properties"));

        // build the signing service
        SigningService signingService = new SigningService(new AisRequestService());
        // build the AIS config and the rest client under the hood
        signingService.initialize(properties);

        // prepare the PDF metadata to sign
        String inputFilePath = properties.getProperty("local.test.inputFile");
        String outputFilePath = properties.getProperty("local.test.outputFilePrefix") + System.currentTimeMillis() + ".pdf";
        PdfMetadata document = new PdfMetadata(inputFilePath, outputFilePath);

        // load even the user data from the properties store
        UserData userData = new UserData()
            .fromProperties(properties)
            .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
            .build();

        SignatureResult result = signingService.performSignings(Collections.singletonList(document), SignatureMode.ON_DEMAND_WITH_STEP_UP, userData);
        System.out.println("Finish to sign the document(s) with the status: " + result);
    }
}
