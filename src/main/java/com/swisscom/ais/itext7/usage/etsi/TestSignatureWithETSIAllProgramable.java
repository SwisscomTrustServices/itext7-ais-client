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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestSignatureWithETSIAllProgramable {
    public static void main(String[] args) throws IOException {

        String inputFilePath = "/empty.pdf";
        String outputFilePath = "prefix" + System.currentTimeMillis() + ".pdf";
        PdfMetadata document = new PdfMetadata(new FileInputStream(inputFilePath), new FileOutputStream(outputFilePath), DigestAlgorithm.SHA256);

        String hashAlgorithmOID = "2.16.840.1.101.3.4.2.1";
        String credentialID = "OnDemand-Qualified4";
        ETSIUserData userData = ETSIUserData.builder()
                .withSignatureName("TEST Signer")
                .withSignatureReason("Testing signature")
                .withSignatureLocation("Testing location")
                .withSignatureContactInfo("tester.test@test.com")
                .withCredentialID(credentialID)
                .withProfile("http://uri.etsi.org/19432/v1.1.1#/creationprofile#")
                .withHashAlgorithmOID(hashAlgorithmOID)
                .withSignatureFormat("P")
                .withConformanceLevel("AdES-B-LTA")
                .withConsentUrlCallback((consentUrl, userData1) -> System.out.println("Consent URL: " + consentUrl))
                .build();

        Trace trace = new Trace(userData.getTransactionId());
        PdfDocumentHandler pdfDocumentHandler = DocumentUtils.prepareOneDocumentForSigning(document, SignatureMode.ETSI, SignatureType.CMS, userData, trace);

        new LogbackConfiguration().initialize(VerboseLevel.BASIC);
        RestClientConfiguration restConfig = RestClientConfiguration.builder()
                .withServiceSignUrl("https://xxxxxxxxx/AIS-Server/etsi/standard/rdsc/v1/signatures/signDoc")
                .withClientKeyFile("/etsi/privateKey.key")
//                .withClientKeyPassword("client.auth.keyPassword")
                .withClientCertificateFile("etsi/certificate.crt")
//                .withServerCertificateFile("server.cert.file")
                .withMaxTotalConnections(20)
                .withMaxConnectionsPerRoute(10)
                .withConnectionTimeoutInSec(10)
                .withResponseTimeoutInSec(20)
                .build();

        ETSIRestClient restClient = new ETSIRestClientImpl().withConfiguration(restConfig);

        try (ETSIAisClient client = new ETSIAisClientImpl(restClient, "path/to/licence")) {
            RAXCodeUrlParameters urlDetails = getRaxCodeUrlParameters(inputFilePath, hashAlgorithmOID, credentialID);
            String codeFromConsole = AuthenticationUtils.getJWTFromConsole(urlDetails, pdfDocumentHandler, true);
            client.signOnDemandWithETSI(pdfDocumentHandler, userData, trace, codeFromConsole);
        }
    }

    private static RAXCodeUrlParameters getRaxCodeUrlParameters(String inputFilePath, String hashAlgorithmOID, String credentialID) {
        RAXCodeUrlParameters urlDetails = new RAXCodeUrlParameters();
        urlDetails.setRaxURL("https://xxxxxxxxxx/en/auth/realms/broker/protocol/openid-connect/auth");
        urlDetails.setState("e034ef94-af77-4665-be5a-20f84589ccf6");
        urlDetails.setNonce("cbebefb1-8935-4d86-886b-a0aafc5db814");
        urlDetails.setCode("code");
        urlDetails.setClient_id("client_id");
        urlDetails.setScope("sign");
        urlDetails.setRedirectURI("https://redirect_rul");
        urlDetails.setChallangeMethod("S256");
        urlDetails.setInputFromFile(inputFilePath);
        urlDetails.setHashAlgorithmOID(hashAlgorithmOID);
        urlDetails.setCredentialID(credentialID);
        return urlDetails;
    }
}
