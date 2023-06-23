package com.swisscom.ais.itext7.client.model;

import com.swisscom.ais.itext7.client.common.PropertiesLoader;

public abstract class AbstractUserData<T> extends PropertiesLoader<T> {
    protected String transactionId;

    protected String signatureName;
    protected String signatureReason;
    protected String signatureLocation;
    protected String signatureContactInfo;

    protected ConsentUrlCallback consentUrlCallback;

    public AbstractUserData() {
    }

    public AbstractUserData(String signatureName, String signatureReason, String signatureLocation, String signatureContactInfo, ConsentUrlCallback consentUrlCallback, String transactionId) {
        this.signatureName = signatureName;
        this.signatureReason = signatureReason;
        this.signatureLocation = signatureLocation;
        this.signatureContactInfo = signatureContactInfo;
        this.consentUrlCallback = consentUrlCallback;
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSignatureName() {
        return signatureName;
    }

    public void setSignatureName(String signatureName) {
        this.signatureName = signatureName;
    }

    public String getSignatureReason() {
        return signatureReason;
    }

    public void setSignatureReason(String signatureReason) {
        this.signatureReason = signatureReason;
    }

    public String getSignatureLocation() {
        return signatureLocation;
    }

    public void setSignatureLocation(String signatureLocation) {
        this.signatureLocation = signatureLocation;
    }

    public String getSignatureContactInfo() {
        return signatureContactInfo;
    }

    public void setSignatureContactInfo(String signatureContactInfo) {
        this.signatureContactInfo = signatureContactInfo;
    }

}
