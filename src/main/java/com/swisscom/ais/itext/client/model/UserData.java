package com.swisscom.ais.itext.client.model;

import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.common.PropertiesLoader;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;
import com.swisscom.ais.itext.client.utils.IdGenerator;
import com.swisscom.ais.itext.client.utils.ValidationUtils;

import org.apache.commons.lang3.StringUtils;

public class UserData extends PropertiesLoader<UserData> {

    private String transactionId;

    private String claimedIdentityName;
    private String claimedIdentityKey;
    private String distinguishedName;

    private String stepUpLanguage;
    private String stepUpMsisdn;
    private String stepUpMessage;
    private String stepUpSerialNumber;

    private String signatureName;
    private String signatureReason;
    private String signatureLocation;
    private String signatureContactInfo;

    private ConsentUrlCallback consentUrlCallback;

    private boolean addTimestamp = true;
    private RevocationInformation addRevocationInformation = RevocationInformation.DEFAULT;
    private SignatureStandard signatureStandard = SignatureStandard.DEFAULT;

    public UserData() {
        setTransactionIdToRandomUuid();
    }

    public UserData(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setTransactionIdToRandomUuid() {
        this.transactionId = IdGenerator.generateId();
    }

    public String getClaimedIdentityName() {
        return claimedIdentityName;
    }

    public void setClaimedIdentityName(String claimedIdentityName) {
        this.claimedIdentityName = claimedIdentityName;
    }

    public String getStepUpLanguage() {
        return stepUpLanguage;
    }

    public void setStepUpLanguage(String stepUpLanguage) {
        this.stepUpLanguage = stepUpLanguage;
    }

    public String getStepUpMsisdn() {
        return stepUpMsisdn;
    }

    public void setStepUpMsisdn(String stepUpMsisdn) {
        this.stepUpMsisdn = stepUpMsisdn;
    }

    public String getStepUpMessage() {
        return stepUpMessage;
    }

    public void setStepUpMessage(String stepUpMessage) {
        this.stepUpMessage = stepUpMessage;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public ConsentUrlCallback getConsentUrlCallback() {
        return consentUrlCallback;
    }

    public void setConsentUrlCallback(ConsentUrlCallback consentUrlCallback) {
        this.consentUrlCallback = consentUrlCallback;
    }

    public UserData withConsentUrlCallback(ConsentUrlCallback consentUrlCallback) {
        setConsentUrlCallback(consentUrlCallback);
        return this;
    }

    public boolean isAddTimestamp() {
        return addTimestamp;
    }

    public void setAddTimestamp(boolean addTimestamp) {
        this.addTimestamp = addTimestamp;
    }

    public RevocationInformation getAddRevocationInformation() {
        return addRevocationInformation;
    }

    public void setAddRevocationInformation(RevocationInformation addRevocationInformation) {
        this.addRevocationInformation = addRevocationInformation;
    }

    public SignatureStandard getSignatureStandard() {
        return signatureStandard;
    }

    public void setSignatureStandard(SignatureStandard signatureStandard) {
        this.signatureStandard = signatureStandard;
    }

    public String getClaimedIdentityKey() {
        return claimedIdentityKey;
    }

    public void setClaimedIdentityKey(String claimedIdentityKey) {
        this.claimedIdentityKey = claimedIdentityKey;
    }

    public String getStepUpSerialNumber() {
        return stepUpSerialNumber;
    }

    public void setStepUpSerialNumber(String stepUpSerialNumber) {
        this.stepUpSerialNumber = stepUpSerialNumber;
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

    @Override
    public UserData getCurrentContext() {
        return this;
    }

    @Override
    public void setFromConfigurationProvider(ConfigurationProvider provider) {
        setClaimedIdentityName(extractStringProperty(provider, "signature.claimedIdentityName"));
        setClaimedIdentityKey(provider.getProperty("signature.claimedIdentityKey"));
        setStepUpLanguage(provider.getProperty("signature.stepUp.language"));
        setStepUpMsisdn(provider.getProperty("signature.stepUp.msisdn"));
        setStepUpMessage(provider.getProperty("signature.stepUp.message"));
        setStepUpSerialNumber(provider.getProperty("signature.stepUp.serialNumber"));
        setDistinguishedName(extractStringProperty(provider, "signature.distinguishedName"));
        setSignatureName(provider.getProperty("signature.name"));
        setSignatureReason(provider.getProperty("signature.reason"));
        setSignatureLocation(provider.getProperty("signature.location"));
        setSignatureContactInfo(provider.getProperty("signature.contactInfo"));
        setSignatureStandard(extractProperty(provider, "signature.standard", SignatureStandard::getByValue, SignatureStandard.DEFAULT));
        setAddRevocationInformation(extractProperty(provider, "signature.revocationInformation", RevocationInformation::getByValue,
                                                    RevocationInformation.DEFAULT));
        setAddTimestamp(extractProperty(provider, "signature.addTimestamp", Boolean::parseBoolean, Boolean.TRUE));
    }

    public void validatePropertiesForSignature(SignatureMode signatureMode, Trace trace) {
        if (StringUtils.isBlank(transactionId)) {
            throw new AisClientException("The user data's transactionId cannot be null or empty. For example, you can set it to a new UUID "
                                         + "or to any other value that is unique between requests. This helps with traceability in the logs "
                                         + "generated by the AIS client");
        }
        ValidationUtils.notNull(claimedIdentityName, "The claimedIdentityName must be provided", trace);
        switch (signatureMode) {
            case TIMESTAMP:
            case STATIC:
            case ON_DEMAND:
                break;
            case ON_DEMAND_WITH_STEP_UP:
                ValidationUtils.allNotNull("The stepUpLanguage, stepUpMessage and stepUpMsisdn must be provided", trace,
                                           stepUpLanguage, stepUpMessage, stepUpMsisdn);
                break;
            default:
                throw new IllegalArgumentException(String.format("Invalid signature mode: %s", signatureMode));
        }
    }
}
