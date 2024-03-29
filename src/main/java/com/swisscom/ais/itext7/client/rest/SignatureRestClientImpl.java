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
package com.swisscom.ais.itext7.client.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swisscom.ais.itext7.client.common.AisClientException;
import com.swisscom.ais.itext7.client.common.Loggers;
import com.swisscom.ais.itext7.client.model.Trace;
import com.swisscom.ais.itext7.client.rest.model.pendingreq.AISPendingRequest;
import com.swisscom.ais.itext7.client.rest.model.signreq.AISSignRequest;
import com.swisscom.ais.itext7.client.rest.model.signresp.AISSignResponse;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.PrivateKeyStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

public class SignatureRestClientImpl implements SignatureRestClient {

    private static final Logger clientLogger = LoggerFactory.getLogger(Loggers.CLIENT);
    private static final Logger protocolLogger = LoggerFactory.getLogger(Loggers.CLIENT_PROTOCOL);
    private static final Logger reqRespLogger = LoggerFactory.getLogger(Loggers.REQUEST_RESPONSE);
    private static final Logger fullReqRespLogger = LoggerFactory.getLogger(Loggers.FULL_REQUEST_RESPONSE);

    private static final String MAIN_ALIAS = "main";
    private static final String STRIP_PATTERN = "\"[A-Za-z0-9+\\\\/=_-]{500,}\"";
    private static final String STRIP_REPLACEMENT = "\"...\"";
    private static final String SIGN_REQUEST_OPERATION = "SignRequest";
    private static final String PENDING_REQUEST_OPERATION = "PendingRequest";

    private RestClientConfiguration config;
    private ObjectMapper jacksonMapper;
    private CloseableHttpClient httpClient;

    public SignatureRestClientImpl withConfiguration(RestClientConfiguration config) {
        this.config = config;
        Security.addProvider(new BouncyCastleProvider());
        jacksonMapper = new ObjectMapper();
        jacksonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jacksonMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        jacksonMapper.setSerializationInclusion(JsonInclude.Include.USE_DEFAULTS);

        SSLConnectionSocketFactory sslConnectionSocketFactory;
        try {
            SSLContextBuilder sslContextBuilder = SSLContexts.custom()
                .loadKeyMaterial(produceTheKeyStore(config), keyToCharArray(config.getClientKeyPassword()), producePrivateKeyStrategy());
            if (StringUtils.isNotBlank(config.getServerCertificateFile())) {
                sslContextBuilder.loadTrustMaterial(produceTheTrustStore(config.getServerCertificateFile()), null);
            }
            sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContextBuilder.build());
        } catch (Exception e) {
            throw new AisClientException("Failed to configure the TLS/SSL connection factory for the AIS client", e);
        }

        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setMaxConnTotal(config.getMaxTotalConnections())
            .setMaxConnPerRoute(config.getMaxConnectionsPerRoute())
            .setSSLSocketFactory(sslConnectionSocketFactory)
            .build();
        RequestConfig httpClientRequestConfig = RequestConfig.custom()
            .setConnectTimeout(config.getConnectionTimeoutInSec(), TimeUnit.SECONDS)
            .setResponseTimeout(config.getResponseTimeoutInSec(), TimeUnit.SECONDS)
            .build();

        httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(httpClientRequestConfig)
            .build();

        return this;
    }

    @Override
    public AISSignResponse requestSignature(AISSignRequest requestWrapper, Trace trace) {
        return executeRequest(SIGN_REQUEST_OPERATION, config.getServiceSignUrl(), requestWrapper, AISSignResponse.class, trace);
    }

    @Override
    public AISSignResponse pollForSignatureStatus(AISPendingRequest requestWrapper, Trace trace) {
        return executeRequest(PENDING_REQUEST_OPERATION, config.getServicePendingUrl(), requestWrapper, AISSignResponse.class, trace);
    }

    private <Req, Resp> Resp executeRequest(String operationName, String serviceUrl, Req requestObject,
                                            @SuppressWarnings("SameParameterValue") Class<Resp> responseClass, Trace trace) {
        protocolLogger.debug("{}: Serializing type object {} to JSON - {}", operationName, requestObject.getClass().getSimpleName(), trace.getId());
        String requestJson = getSerializedRequest(operationName, requestObject, trace);

        HttpPost httpPost = new HttpPost(serviceUrl);
        httpPost.setEntity(new StringEntity(requestJson, ContentType.APPLICATION_JSON, CharEncoding.UTF_8, false));
        httpPost.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON);

        protocolLogger.info("{}: Sending request to: [{}] - {}", operationName, serviceUrl, trace.getId());
        reqRespLogger.info("{}: Sending JSON to: [{}], content: [{}] - {}", operationName, serviceUrl, requestJson, trace.getId());
        fullReqRespLogger.info("{}: Sending JSON to: [{}], content: [{}] - {}", operationName, serviceUrl, requestJson, trace.getId());

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            protocolLogger.info("{}: Received HTTP status code: {} - {}", operationName, response.getCode(), trace.getId());
            String responseJson = getSerializedResponse(operationName, trace, response);
            if (response.getCode() == 200) {
                logInfo(operationName, AISSignResponse.class, trace, responseJson);
                return readSerializedResponse(operationName, responseClass, trace, responseJson);
            }
            throw new AisClientException(String.format("Received fault response: HTTP %d %s - %s", response.getCode(), response.getReasonPhrase(),
                                                       trace.getId()));
        } catch (SSLException e) {
            throw new AisClientException(String.format("TLS/SSL connection failure for %s - %s", operationName, trace.getId()), e);
        } catch (Exception e) {
            throw new AisClientException(String.format("Communication failure for %s - %s", operationName, trace.getId()), e);
        }
    }

    private <Resp> void logInfo(String operationName, @SuppressWarnings("SameParameterValue") Class<Resp> responseClass, Trace trace,
                                String responseJson) {
        if (reqRespLogger.isInfoEnabled()) {
            String strippedResponse = stripInnerLargeBase64Content(responseJson);
            reqRespLogger.info("{}: Received JSON content: {} - {}", operationName, strippedResponse, trace.getId());
        }
        if (fullReqRespLogger.isInfoEnabled()) {
            fullReqRespLogger.info("{}: Received JSON content: {} - {}", operationName, responseJson, trace.getId());
        }
        protocolLogger.debug("{}: Deserializing JSON to object of type {} - {}", operationName, responseClass.getSimpleName(), trace.getId());
    }

    private String stripInnerLargeBase64Content(String source) {
        return source.replaceAll(STRIP_PATTERN, STRIP_REPLACEMENT);
    }

    private <Req> String getSerializedRequest(String operationName, Req requestObject, Trace trace) {
        try {
            return jacksonMapper.writeValueAsString(requestObject);
        } catch (JsonProcessingException e) {
            throw new AisClientException(String.format("Failed to serialize request object to JSON, for operation %s - %s", operationName,
                                                       trace.getId()), e);
        }
    }

    private String getSerializedResponse(String operationName, Trace trace, CloseableHttpResponse response) throws IOException {
        try {
            return EntityUtils.toString(response.getEntity());
        } catch (ParseException e) {
            throw new AisClientException(String.format("Failed to interpret the HTTP response content as a string, for operation %s - %s",
                                                       operationName, trace.getId()), e);
        }
    }

    private <Resp> Resp readSerializedResponse(String operationName, Class<Resp> responseClass, Trace trace, String responseJson) {
        try {
            return jacksonMapper.readValue(responseJson, responseClass);
        } catch (JsonProcessingException e) {
            throw new AisClientException(String.format("Failed to deserialize JSON content to object of type %s  for operation %s - %s",
                                                       responseClass.getSimpleName(), operationName, trace.getId()), e);
        }
    }

    @Override
    public void close() throws IOException {
        clientLogger.debug("Closing the REST client");
        if (Objects.nonNull(httpClient)) {
            clientLogger.debug("Closing the embedded HTTP client");
            httpClient.close();
        }
    }

    private KeyStore produceTheKeyStore(RestClientConfiguration config) {
        try {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            FileInputStream is = new FileInputStream(config.getClientCertificateFile());
            X509Certificate certificate = (X509Certificate) fact.generateCertificate(is);
            PrivateKey privateKey = getPrivateKey(config.getClientKeyFile(), config.getClientKeyPassword());

            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(null, null);
            keyStore.setKeyEntry(MAIN_ALIAS, privateKey, keyToCharArray(config.getClientKeyPassword()), new Certificate[]{certificate});

            return keyStore;
        } catch (Exception e) {
            throw new AisClientException("Failed to initialize the TLS keystore", e);
        }
    }

    private KeyStore produceTheTrustStore(String serverCertificateFile) {
        try (FileInputStream is = new FileInputStream(serverCertificateFile)) {
            CertificateFactory fact = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) fact.generateCertificate(is);

            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(null, null);
            keyStore.setCertificateEntry(MAIN_ALIAS, certificate);

            return keyStore;
        } catch (Exception e) {
            throw new AisClientException("Failed to initialize the TLS truststore", e);
        }
    }

    public static PrivateKey getPrivateKey(String fileName, String keyPassword) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            // if we read a X509 key we will get immediately a PrivateKeyInfo
            // if the key is a RSA key it is necessary to create a PEMKeyPair first
            PrivateKeyInfo privateKeyInfo;
            PEMParser pemParser;
            try {
                pemParser = new PEMParser(br);
                privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            } catch (Exception ignored) {
                br.close();
                br = new BufferedReader(new FileReader(fileName));
                pemParser = new PEMParser(br);
                Object pemKeyPair = pemParser.readObject();
                if (pemKeyPair instanceof PEMEncryptedKeyPair) {
                    if (StringUtils.isBlank(keyPassword)) {
                        throw new AisClientException("The client private key is encrypted but there is no key password provided " +
                                                     "(check field 'client.auth.keyPassword' from the config.properties or from " +
                                                     "the REST client configuration)");
                    }
                    PEMDecryptorProvider decryptionProv = new JcePEMDecryptorProviderBuilder().build(keyPassword.toCharArray());
                    PEMKeyPair decryptedKeyPair = ((PEMEncryptedKeyPair) pemKeyPair).decryptKeyPair(decryptionProv);
                    privateKeyInfo = decryptedKeyPair.getPrivateKeyInfo();
                } else {
                    privateKeyInfo = ((PEMKeyPair) pemKeyPair).getPrivateKeyInfo();
                }
            }

            pemParser.close();
            br.close();

            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
            return jcaPEMKeyConverter.getPrivateKey(privateKeyInfo);
        } catch (Exception e) {
            throw new AisClientException("Failed to initialize the client private key", e);
        }
    }

    private PrivateKeyStrategy producePrivateKeyStrategy() {
        return (aliases, sslParameters) -> MAIN_ALIAS;
    }

    private char[] keyToCharArray(String key) {
        return StringUtils.isBlank(key) ? new char[0] : key.toCharArray();
    }
}
