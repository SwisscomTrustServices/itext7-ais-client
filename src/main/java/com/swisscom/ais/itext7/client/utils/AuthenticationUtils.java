package com.swisscom.ais.itext7.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisscom.ais.itext7.client.config.ConfigurationProvider;
import com.swisscom.ais.itext7.client.config.ConfigurationProviderPropertiesImpl;
import com.swisscom.ais.itext7.client.impl.PdfDocumentHandler;
import com.swisscom.ais.itext7.client.rest.model.signreq.etsi.rax.AuthRequest;
import com.swisscom.ais.itext7.client.rest.model.signreq.etsi.rax.DocumentsDigests;

import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;

public class AuthenticationUtils {

    public static String getJWTFromConsole(Properties properties, PdfDocumentHandler prepareDocumentForSigning) throws JsonProcessingException {
        String claims = claims(properties, prepareDocumentForSigning);
        String url = createRAXUrl(properties, claims);
        System.out.println("click url to retrieve JWT code: " + url);
        boolean shouldOpenBrowser = Boolean.parseBoolean(properties.getProperty("open.browser"));
        if (shouldOpenBrowser) {
            openBrowserToRAX(url);
        }
        System.out.println("Waiting JWT auth - code: ");
        Scanner keyboard = new Scanner(System.in);
        return keyboard.nextLine();
    }

    private static String claims(Properties properties, PdfDocumentHandler prepareDocumentForSigning) throws JsonProcessingException {
        String inputFromFile = properties.getProperty("local.test.inputFile");
        AuthRequest raxAuthRequest = new AuthRequest();
        raxAuthRequest.setHashAlgorithmOID(properties.getProperty("etsi.hash.algorithmOID"));
        raxAuthRequest.setCredentialID(properties.getProperty("etsi.credentialID"));
        String[] split = inputFromFile.split("/");
        DocumentsDigests documentsDigests = new DocumentsDigests(prepareDocumentForSigning.getEncodedDocumentHash()
                , split[split.length - 1]);
        raxAuthRequest.setDocumentDigests(Collections.singletonList(documentsDigests));
        return new ObjectMapper().writeValueAsString(raxAuthRequest);
    }

    private static String createRAXUrl(Properties properties, String claimsJson) {
        ConfigurationProviderPropertiesImpl prov = new ConfigurationProviderPropertiesImpl(properties);
        String raxURL = getStringNotNull(prov, "etsi.rax.url");
        String state = getStringNotNull(prov, "etsi.rax.state");
        String nonce = getStringNotNull(prov, "etsi.rax.nonce");
        String code = getPropOrDefault(prov, "etsi.rax.response_type", "code");
        String client_id = getStringNotNull(prov, "etsi.rax.client_id");
        String scope = getPropOrDefault(prov, "etsi.rax.scope", "sign");
        String redirectURI = getStringNotNull(prov, "etsi.rax.redirect_uri");
        String challangeMethod = getStringNotNull(prov, "etsi.rax.code_challenge_method");

        return raxURL +
                "?" + "state" + "=" + URLEncoder.encode(state) +
                "&" + "nonce" + "=" + URLEncoder.encode(nonce) +
                "&" + "response_type" + "=" + URLEncoder.encode(code) +
                "&" + "client_id" + "=" + URLEncoder.encode(client_id) +
                "&" + "scope" + "=" + URLEncoder.encode(scope) +
                "&" + "redirect_uri" + "=" + URLEncoder.encode(redirectURI) +
                "&" + "code_challenge_method" + "=" + URLEncoder.encode(challangeMethod) +
                "&" + "claims" + "=" + URLEncoder.encode(claimsJson);
    }

    private static String getPropOrDefault(ConfigurationProviderPropertiesImpl configurationProviderProperties, String prop, String defaultVale) {
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
