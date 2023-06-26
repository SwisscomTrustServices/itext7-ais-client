package com.swisscom.ais.itext7.usage.etsi;

import com.swisscom.ais.itext7.client.ETSIAisClient;
import com.swisscom.ais.itext7.client.config.LogbackConfiguration;
import com.swisscom.ais.itext7.client.impl.ETSIAisClientImpl;
import com.swisscom.ais.itext7.client.impl.PdfDocumentHandler;
import com.swisscom.ais.itext7.client.model.*;
import com.swisscom.ais.itext7.client.rest.ETSIRestClient;
import com.swisscom.ais.itext7.client.rest.ETSIRestClientImpl;
import com.swisscom.ais.itext7.client.rest.RestClientConfiguration;
import com.swisscom.ais.itext7.client.rest.model.RAXCodeUrlParameters;
import com.swisscom.ais.itext7.client.utils.AuthenticationUtils;
import com.swisscom.ais.itext7.client.utils.DocumentUtils;
import com.swisscom.ais.itext7.usage.dss.SignatureTest;
import com.swisscom.ais.itext7.usage.dss.TestOnDemandSignature;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TestSignatureWithETSI extends SignatureTest {

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(TestOnDemandSignature.class.getResourceAsStream("/cli/etsi-config.properties"));

        String inputFilePath = properties.getProperty("local.test.inputFile");
        String outputFilePath = properties.getProperty("local.test.outputFilePrefix") + System.currentTimeMillis() + ".pdf";
        PdfMetadata document = new PdfMetadata(new FileInputStream(inputFilePath), new FileOutputStream(outputFilePath), DigestAlgorithm.SHA256);

        ETSIUserData userData = new ETSIUserData()
                .fromProperties(properties)
                .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
                .build();
        Trace trace = new Trace(userData.getTransactionId());
        PdfDocumentHandler pdfDocumentHandler = DocumentUtils.prepareOneDocumentForSigning(document, SignatureMode.ETSI, SignatureType.CMS, userData, trace);

        new LogbackConfiguration().initialize(VerboseLevel.BASIC);
        RestClientConfiguration restConfig = new RestClientConfiguration().fromProperties(properties).build();
        ETSIRestClient restClient = new ETSIRestClientImpl().withConfiguration(restConfig);

        try (ETSIAisClient client = new ETSIAisClientImpl(restClient, properties.getProperty("license.file"))) {
            String codeFromConsole = AuthenticationUtils.getJWTFromConsole(new RAXCodeUrlParameters().fromProperties(properties), pdfDocumentHandler, true);
            client.signOnDemandWithETSI(pdfDocumentHandler, userData, trace, codeFromConsole);
        }
    }
}
