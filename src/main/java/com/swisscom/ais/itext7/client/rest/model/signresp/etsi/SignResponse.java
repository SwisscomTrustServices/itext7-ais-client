package com.swisscom.ais.itext7.client.rest.model.signresp.etsi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SignResponse {

    @JsonProperty("SignatureObject")
    private List<String> signatureObject;

    @JsonProperty("validationInfo")
    private EtsiValidationInfo etsiValidationInfo;

    public List<String> getSignatureObject() {
        return signatureObject;
    }

    @JsonProperty("SignatureObject")
    public void setSignatureObject(List<String> signatureObject) {
        this.signatureObject = signatureObject;
    }

    public EtsiValidationInfo getEtsiValidationInfo() {
        return etsiValidationInfo;
    }
    @JsonProperty("validationInfo")
    public void setEtsiValidationInfo(EtsiValidationInfo etsiValidationInfo) {
        this.etsiValidationInfo = etsiValidationInfo;
    }
}
