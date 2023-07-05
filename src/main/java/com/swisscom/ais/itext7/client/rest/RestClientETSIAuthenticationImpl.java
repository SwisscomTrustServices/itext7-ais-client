package com.swisscom.ais.itext7.client.rest;

import com.swisscom.ais.itext7.client.common.AisClientException;
import com.swisscom.ais.itext7.client.model.Trace;
import com.swisscom.ais.itext7.client.rest.model.etsi.auth.TokenRequest;
import com.swisscom.ais.itext7.client.rest.model.etsi.auth.TokenResponse;
import com.swisscom.ais.itext7.client.rest.model.signresp.dss.AISSignResponse;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import javax.net.ssl.SSLException;
import java.util.ArrayList;
import java.util.List;

public class RestClientETSIAuthenticationImpl extends AbstractRestClient implements RestClientETSIAuthentication {

    public RestClientETSIAuthenticationImpl withConfiguration(RestClientConfiguration config) {
        super.autoConf(config);
        return this;
    }

    protected TokenResponse executeRequest(String serviceUrl, TokenRequest tokenRequest, Trace trace) {
        String operationName = "GetETSIJWT";
        HttpPost httpPost = new HttpPost(serviceUrl);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("code", tokenRequest.getCode()));
        nameValuePairList.add(new BasicNameValuePair("client_id", tokenRequest.getClient_id()));
        nameValuePairList.add(new BasicNameValuePair("client_secret", tokenRequest.getClient_secret()));
        nameValuePairList.add(new BasicNameValuePair("grant_type", "authorization_code"));
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));

        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.getMimeType());

        protocolLogger.info("{}: Sending request to: [{}] - {}", operationName, serviceUrl, trace.getId());

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            protocolLogger.info("{}: Received HTTP status code: {} - {}", operationName, response.getCode(), trace.getId());
            String responseJson = getSerializedResponse(operationName, trace, response);
            if (response.getCode() == 201) {
                logInfo(operationName, AISSignResponse.class, trace, responseJson);
                return readSerializedResponse(operationName, TokenResponse.class, trace, responseJson);
            }
            throw new AisClientException(String.format("Received fault response: HTTP %d %s - %s", response.getCode(), response.getReasonPhrase(),
                    trace.getId()));
        } catch (SSLException e) {
            throw new AisClientException(String.format("TLS/SSL connection failure for %s - %s", operationName, trace.getId()), e);
        } catch (Exception e) {
            throw new AisClientException(String.format("Communication failure for %s - %s", operationName, trace.getId()), e);
        }
    }

    public TokenResponse getToken(TokenRequest tokenRequest, Trace trace) {
        return executeRequest(config.getServiceSignUrl(), tokenRequest, trace);
    }
}
