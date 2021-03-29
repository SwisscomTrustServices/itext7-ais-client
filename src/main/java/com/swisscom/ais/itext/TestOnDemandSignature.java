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

/**
 * Test with an On Demand signature with Step Up that shows how to access all the configuration available and load it from a
 * properties file. The same configuration can also be tweaked by hand or via some framework (e.g. Spring, Guice, etc).
 */
public class TestOnDemandSignature {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.load(TestOnDemandSignature.class.getResourceAsStream("/local-config.properties"));

        new LogbackConfiguration().initialize(VerboseLevel.BASIC);
        SigningService signingService = new SigningService(new AisRequestService());
        signingService.initialize(properties);

        ArgumentsContext context = ArgumentsContext.builder()
            .withInputFile(properties.getProperty("local.test.inputFile"))
            .withOutputFile(properties.getProperty("local.test.outputFilePrefix") + System.currentTimeMillis() + ".pdf")
            .withSignature(SignatureMode.ON_DEMAND)
            .build();
        UserData userData = new UserData()
            .fromProperties(properties)
            .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
            .build();
        SignatureResult signatureResult = signingService.performSignings(context, userData);
        System.out.println("Finish to sign the document(s) with the status: " + signatureResult);
    }
}
