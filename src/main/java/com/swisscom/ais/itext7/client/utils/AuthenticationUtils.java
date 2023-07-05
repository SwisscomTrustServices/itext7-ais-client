package com.swisscom.ais.itext7.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisscom.ais.itext7.client.config.ConfigurationProvider;
import com.swisscom.ais.itext7.client.config.ConfigurationProviderPropertiesImpl;
import com.swisscom.ais.itext7.client.impl.PdfDocumentHandler;
import com.swisscom.ais.itext7.client.model.Trace;
import com.swisscom.ais.itext7.client.rest.RestClientConfiguration;
import com.swisscom.ais.itext7.client.rest.RestClientETSIAuthenticationImpl;
import com.swisscom.ais.itext7.client.rest.model.RAXCodeUrlParameters;
import com.swisscom.ais.itext7.client.rest.model.etsi.auth.TokenRequest;
import com.swisscom.ais.itext7.client.rest.model.signreq.etsi.rax.AuthRequest;
import com.swisscom.ais.itext7.client.rest.model.signreq.etsi.rax.DocumentsDigests;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;

public class AuthenticationUtils {


    public static String getJwtToken(Properties properties, Trace trace, PdfDocumentHandler pdfDocumentHandler, RestClientConfiguration etsiMtlsRestConfig) throws IOException {
        try (RestClientETSIAuthenticationImpl restClientETSIAuthentication = new RestClientETSIAuthenticationImpl().withConfiguration(etsiMtlsRestConfig)) {
            String codeFromConsole = getCodeForJWTFromConsole(new RAXCodeUrlParameters().fromProperties(properties), pdfDocumentHandler, true);

            TokenRequest tokenRequest = new TokenRequest();
            tokenRequest.setCode(codeFromConsole);
            tokenRequest.setClient_id(properties.getProperty("etsi.jwt.clientId"));
            tokenRequest.setClient_secret(properties.getProperty("etsi.jwt.client.secret"));
            return restClientETSIAuthentication.getToken(tokenRequest, trace).getAccess_token();
        }
    }

    public static String getCodeForJWTFromConsole(RAXCodeUrlParameters urlDetails, PdfDocumentHandler prepareDocumentForSigning, boolean shouldOpenBrowser) throws JsonProcessingException {
        String claims = claims(urlDetails, prepareDocumentForSigning);
        String url = createRAXUrl(urlDetails, claims);
        System.out.println("click url to retrieve JWT code: " + url);
        if (shouldOpenBrowser) {
            openBrowserToRAX(url);
        }
        System.out.println("Waiting JWT auth - code: ");
        Scanner keyboard = new Scanner(System.in);
        return keyboard.nextLine();
    }

    private static String claims(RAXCodeUrlParameters urlDetails, PdfDocumentHandler prepareDocumentForSigning) throws JsonProcessingException {
        AuthRequest raxAuthRequest = new AuthRequest();
        raxAuthRequest.setHashAlgorithmOID(urlDetails.getHashAlgorithmOID());
        raxAuthRequest.setCredentialID(urlDetails.getCredentialID());
        String[] split = urlDetails.getInputFromFile().split("/");
        DocumentsDigests documentsDigests = new DocumentsDigests(prepareDocumentForSigning.getEncodedDocumentHash()
                , split[split.length - 1]);
        raxAuthRequest.setDocumentDigests(Collections.singletonList(documentsDigests));
        return new ObjectMapper().writeValueAsString(raxAuthRequest);
    }

    private static String createRAXUrl(RAXCodeUrlParameters urlDetails, String claimsJson) {
        return urlDetails.getRaxURL() +
                "?" + "state" + "=" + URLEncoder.encode(urlDetails.getState()) +
                "&" + "nonce" + "=" + URLEncoder.encode(urlDetails.getNonce()) +
                "&" + "response_type" + "=" + URLEncoder.encode(urlDetails.getCode()) +
                "&" + "client_id" + "=" + URLEncoder.encode(urlDetails.getClient_id()) +
                "&" + "scope" + "=" + URLEncoder.encode(urlDetails.getScope()) +
                "&" + "redirect_uri" + "=" + URLEncoder.encode(urlDetails.getRedirectURI()) +
                "&" + "code_challenge_method" + "=" + URLEncoder.encode(urlDetails.getChallangeMethod()) +
                "&" + "claims" + "=" + URLEncoder.encode(claimsJson);
    }

    private static String getPropOrDefault(ConfigurationProviderPropertiesImpl configurationProviderProperties, String prop, String defaultVale) {
        return configurationProviderProperties.getProperty(prop) != null ? configurationProviderProperties.getProperty(prop) : defaultVale;
    }

    public static String getPropOrDefault(ConfigurationProvider configurationProviderProperties, String prop, String defaultVale) {
        return configurationProviderProperties.getProperty(prop) != null ? configurationProviderProperties.getProperty(prop) : defaultVale;
    }

    public static String getStringNotNull(ConfigurationProvider provider, String propertyName) {
        String value = provider.getProperty(propertyName);
        if (value == null) {
            throw new IllegalStateException("Invalid configuration. The [" + propertyName + "] is missing or is empty");
        }
        return value;
    }

    private static void openBrowserToRAX(String url) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception e) {
                System.out.println("Not able to open brower, open it manually");
            }

        }
    }
}
