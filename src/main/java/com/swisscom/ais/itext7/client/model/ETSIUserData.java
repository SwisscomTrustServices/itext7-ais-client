package com.swisscom.ais.itext7.client.model;

import com.swisscom.ais.itext7.client.config.ConfigurationProvider;
import com.swisscom.ais.itext7.client.utils.IdGenerator;

public class ETSIUserData extends AbstractUserData<ETSIUserData.Builder> {

    private String credentialID;
    private String profile;
    private String hashAlgorithmOID;
    private String signatureFormat;
    private String conformanceLevel;

    @Override
    protected ETSIUserData.Builder fromConfigurationProvider(ConfigurationProvider provider) {
        return builder()
                .withSignatureName(provider.getProperty("signature.name"))
                .withSignatureReason(provider.getProperty("signature.reason"))
                .withSignatureLocation(provider.getProperty("signature.location"))
                .withSignatureContactInfo(provider.getProperty("signature.contactInfo"))
                .withCredentialID(provider.getProperty("etsi.credentialID"))
                .withProfile(provider.getProperty("etsi.profile"))
                .withHashAlgorithmOID(provider.getProperty("etsi.hash.algorithmOID"))
                .withSignatureFormat(provider.getProperty("etsi.signature.format"))
                .withConformanceLevel(provider.getProperty("etsi.signature.conformance.level"));
    }

    public ETSIUserData() {
        super();
    }

    public ETSIUserData(String signatureName, String signatureReason, String signatureLocation, String signatureContactInfo, String credentialID, String profile, String hashAlgorithmOID, String signatureFormat, String conformanceLevel, ConsentUrlCallback consentUrlCallback, String transactionID) {
        super(signatureName, signatureReason, signatureLocation, signatureContactInfo, consentUrlCallback, transactionID);
        this.credentialID = credentialID;
        this.profile = profile;
        this.hashAlgorithmOID = hashAlgorithmOID;
        this.signatureFormat = signatureFormat;
        this.conformanceLevel = conformanceLevel;
    }

    public static ETSIUserData.Builder builder() {
        return new ETSIUserData.Builder();

    }

    public static class Builder {
        private final String transactionId = IdGenerator.generateId();

        private String credentialID;
        private String profile;
        private String hashAlgorithmOID;
        private String signatureFormat;
        private String conformanceLevel;
        protected String signatureName;
        protected String signatureReason;
        protected String signatureLocation;
        protected String signatureContactInfo;
        protected ConsentUrlCallback consentUrlCallback;

        public Builder withCredentialID(String credentialID) {
            this.credentialID = credentialID;
            return this;
        }

        public Builder withProfile(String profile) {
            this.profile = profile;
            return this;
        }

        public Builder withHashAlgorithmOID(String hashAlgorithmOID) {
            this.hashAlgorithmOID = hashAlgorithmOID;
            return this;
        }

        public Builder withSignatureFormat(String signatureFormat) {
            this.signatureFormat = signatureFormat;
            return this;
        }

        public Builder withConformanceLevel(String conformanceLevel) {
            this.conformanceLevel = conformanceLevel;
            return this;
        }

        public Builder withSignatureName(String signatureName) {
            this.signatureName = signatureName;
            return this;
        }

        public Builder withSignatureReason(String signatureReason) {
            this.signatureReason = signatureReason;
            return this;
        }

        public Builder withSignatureLocation(String signatureLocation) {
            this.signatureLocation = signatureLocation;
            return this;
        }

        public Builder withSignatureContactInfo(String signatureContactInfo) {
            this.signatureContactInfo = signatureContactInfo;
            return this;
        }

        public Builder withConsentUrlCallback(ConsentUrlCallback consentUrlCallback) {
            this.consentUrlCallback = consentUrlCallback;
            return this;
        }

        public ETSIUserData build() {
            return new ETSIUserData(signatureName, signatureReason, signatureLocation,
                    signatureContactInfo, credentialID,
                    profile,
                    hashAlgorithmOID,
                    signatureFormat,
                    conformanceLevel,
                    consentUrlCallback,
                    transactionId);
        }
    }

    public String getCredentialID() {
        return credentialID;
    }

    public String getProfile() {
        return profile;
    }

    public String getHashAlgorithmOID() {
        return hashAlgorithmOID;
    }

    public String getSignatureFormat() {
        return signatureFormat;
    }

    public String getConformanceLevel() {
        return conformanceLevel;
    }
}
