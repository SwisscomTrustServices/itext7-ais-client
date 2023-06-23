package com.swisscom.ais.itext7.client.rest.model.signreq.etsi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SignRequest {


    private String operationMode;
    private String SAD;
    private String requestID;
    private String credentialID;
    private String lang;
    private String profile;

    private String signatureFormat;
    private String policy;
    private String signaturePolicyID;
    private String documents;
    private DocumentDigests documentDigests;

    private String conformanceLevel;

    public String getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(String operationMode) {
        this.operationMode = operationMode;
    }

    public String getSAD() {
        return SAD;
    }

    @JsonProperty("SAD")
    public void setSAD(String SAD) {
        this.SAD = SAD;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestId) {
        this.requestID = requestId;
    }

    public String getCredentialID() {
        return credentialID;
    }

    public void setCredentialID(String credentialID) {
        this.credentialID = credentialID;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getSignatureFormat() {
        return signatureFormat;
    }

    public void setSignatureFormat(String signatureFormat) {
        this.signatureFormat = signatureFormat;
    }

    public String getSignaturePolicyID() {
        return signaturePolicyID;
    }

    public void setSignaturePolicyID(String signaturePolicyID) {
        this.signaturePolicyID = signaturePolicyID;
    }

    public String getDocuments() {
        return documents;
    }

    public void setDocuments(String documents) {
        this.documents = documents;
    }

    public DocumentDigests getDocumentDigests() {
        return documentDigests;
    }

    public void setDocumentDigests(DocumentDigests documentDigests) {
        this.documentDigests = documentDigests;
    }

    public String getConformanceLevel() {
        return conformanceLevel;
    }

    public void setConformanceLevel(String conformanceLevel) {
        this.conformanceLevel = conformanceLevel;
    }

}
