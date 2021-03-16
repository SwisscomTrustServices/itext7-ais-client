package com.swisscom.ais.itext.client.model;

import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProviderPropertiesImpl;
import com.swisscom.ais.itext.client.utils.PropertyUtils;
import com.swisscom.ais.itext.client.utils.ValidationUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Properties;
import java.util.UUID;

public class UserData {

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
        this.transactionId = UUID.randomUUID().toString();
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

    public void setFromPropertiesClasspathFile(String fileName) {
        setFromProperties(PropertyUtils.loadPropertiesFromClasspathFile(this.getClass(), fileName));
    }

    public void setFromPropertiesFile(String fileName) {
        setFromProperties(PropertyUtils.loadPropertiesFromFile(fileName));
    }

    public void setFromProperties(Properties properties) {
        setFromConfigurationProvider(new ConfigurationProviderPropertiesImpl(properties));
    }

    public void setFromConfigurationProvider(ConfigurationProvider provider) {
        claimedIdentityName = PropertyUtils.extractStringProperty(provider, "signature.claimedIdentityName");
        claimedIdentityKey = provider.getProperty("signature.claimedIdentityKey");
        stepUpLanguage = provider.getProperty("signature.stepUp.language");
        stepUpMsisdn = provider.getProperty("signature.stepUp.msisdn");
        stepUpMessage = provider.getProperty("signature.stepUp.message");
        stepUpSerialNumber = provider.getProperty("signature.stepUp.serialNumber");
        distinguishedName = PropertyUtils.extractStringProperty(provider, "signature.distinguishedName");
        signatureName = provider.getProperty("signature.name");
        signatureReason = provider.getProperty("signature.reason");
        signatureLocation = provider.getProperty("signature.location");
        signatureContactInfo = provider.getProperty("signature.contactInfo");
        signatureStandard = PropertyUtils.extractProperty(provider, "signature.standard", SignatureStandard::getByValue, SignatureStandard.DEFAULT);
        addRevocationInformation = PropertyUtils.extractProperty(provider, "signature.revocationInformation", RevocationInformation::getByValue,
                                                                 RevocationInformation.DEFAULT);
        addTimestamp = PropertyUtils.extractProperty(provider, "signature.addTimestamp", Boolean::parseBoolean, Boolean.TRUE);
    }

    public void validate(SignatureMode signatureMode, Trace trace) {
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
