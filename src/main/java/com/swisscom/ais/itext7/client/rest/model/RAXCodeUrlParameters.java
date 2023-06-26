package com.swisscom.ais.itext7.client.rest.model;



import com.swisscom.ais.itext7.client.config.ConfigurationProviderPropertiesImpl;
import com.swisscom.ais.itext7.client.utils.AuthenticationUtils;

import java.util.Properties;

public class RAXCodeUrlParameters {
    private String inputFromFile;
    private String hashAlgorithmOID;
    private String credentialID;
    private String raxURL;
    private String state;
    private String nonce;
    private String code;
    private String client_id;
    private String scope;
    private String redirectURI;
    private String challangeMethod;

    public String getInputFromFile() {
        return inputFromFile;
    }

    public void setInputFromFile(String inputFromFile) {
        this.inputFromFile = inputFromFile;
    }

    public String getHashAlgorithmOID() {
        return hashAlgorithmOID;
    }

    public void setHashAlgorithmOID(String hashAlgorithmOID) {
        this.hashAlgorithmOID = hashAlgorithmOID;
    }

    public String getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(String credentialID) {
        this.credentialID = credentialID;
    }

    public String getRaxURL() {
        return raxURL;
    }

    public void setRaxURL(String raxURL) {
        this.raxURL = raxURL;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRedirectURI() {
        return redirectURI;
    }

    public void setRedirectURI(String redirectURI) {
        this.redirectURI = redirectURI;
    }

    public String getChallangeMethod() {
        return challangeMethod;
    }

    public void setChallangeMethod(String challangeMethod) {
        this.challangeMethod = challangeMethod;
    }

    public RAXCodeUrlParameters fromProperties(Properties properties) {
        ConfigurationProviderPropertiesImpl prov = new ConfigurationProviderPropertiesImpl(properties);
        raxURL = AuthenticationUtils.getStringNotNull(prov, "etsi.rax.url");
        state = AuthenticationUtils.getStringNotNull(prov, "etsi.rax.state");
        nonce = AuthenticationUtils.getStringNotNull(prov, "etsi.rax.nonce");
        code = AuthenticationUtils.getPropOrDefault(prov, "etsi.rax.response_type", "code");
        client_id = AuthenticationUtils.getStringNotNull(prov, "etsi.rax.client_id");
        scope = AuthenticationUtils.getPropOrDefault(prov, "etsi.rax.scope", "sign");
        redirectURI = AuthenticationUtils.getStringNotNull(prov, "etsi.rax.redirect_uri");
        challangeMethod = AuthenticationUtils.getStringNotNull(prov, "etsi.rax.code_challenge_method");

        inputFromFile = AuthenticationUtils.getStringNotNull(prov, "local.test.inputFile");
        hashAlgorithmOID = AuthenticationUtils.getStringNotNull(prov, "etsi.hash.algorithmOID");
        credentialID = AuthenticationUtils.getStringNotNull(prov, "etsi.credentialID");


        return this;
    }
}
