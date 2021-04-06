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
package com.swisscom.ais.itext.client.model;

import com.swisscom.ais.itext.client.common.AisClientException;
import com.swisscom.ais.itext.client.common.PropertiesLoader;
import com.swisscom.ais.itext.client.common.provider.ConfigurationProvider;
import com.swisscom.ais.itext.client.utils.IdGenerator;
import com.swisscom.ais.itext.client.utils.ValidationUtils;

import org.apache.commons.lang3.StringUtils;

public class UserData extends PropertiesLoader<UserData.Builder> {
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

    private boolean addTimestamp;
    private RevocationInformation revocationInformation;
    private SignatureStandard signatureStandard;

    public UserData() {
    }

    public UserData(String transactionId, String claimedIdentityName, String claimedIdentityKey, String distinguishedName,
                    String stepUpLanguage, String stepUpMsisdn, String stepUpMessage, String stepUpSerialNumber, String signatureName,
                    String signatureReason, String signatureLocation, String signatureContactInfo,
                    ConsentUrlCallback consentUrlCallback, boolean addTimestamp,
                    RevocationInformation revocationInformation, SignatureStandard signatureStandard) {
        this.transactionId = transactionId;
        this.claimedIdentityName = claimedIdentityName;
        this.claimedIdentityKey = claimedIdentityKey;
        this.distinguishedName = distinguishedName;
        this.stepUpLanguage = stepUpLanguage;
        this.stepUpMsisdn = stepUpMsisdn;
        this.stepUpMessage = stepUpMessage;
        this.stepUpSerialNumber = stepUpSerialNumber;
        this.signatureName = signatureName;
        this.signatureReason = signatureReason;
        this.signatureLocation = signatureLocation;
        this.signatureContactInfo = signatureContactInfo;
        this.consentUrlCallback = consentUrlCallback;
        this.addTimestamp = addTimestamp;
        this.revocationInformation = revocationInformation;
        this.signatureStandard = signatureStandard;
    }

    public static UserData.Builder builder() {
        return new Builder();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getClaimedIdentityName() {
        return claimedIdentityName;
    }

    public String getStepUpLanguage() {
        return stepUpLanguage;
    }

    public String getStepUpMsisdn() {
        return stepUpMsisdn;
    }

    public String getStepUpMessage() {
        return stepUpMessage;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public ConsentUrlCallback getConsentUrlCallback() {
        return consentUrlCallback;
    }

    public boolean isAddTimestamp() {
        return addTimestamp;
    }

    public RevocationInformation getRevocationInformation() {
        return revocationInformation;
    }

    public SignatureStandard getSignatureStandard() {
        return signatureStandard;
    }

    public String getClaimedIdentityKey() {
        return claimedIdentityKey;
    }

    public String getStepUpSerialNumber() {
        return stepUpSerialNumber;
    }

    public String getSignatureName() {
        return signatureName;
    }

    public String getSignatureReason() {
        return signatureReason;
    }

    public String getSignatureLocation() {
        return signatureLocation;
    }

    public String getSignatureContactInfo() {
        return signatureContactInfo;
    }

    @Override
    protected UserData.Builder fromConfigurationProvider(ConfigurationProvider provider) {
        return builder()
            .withClaimedIdentityName(extractStringProperty(provider, "signature.claimedIdentityName"))
            .withClaimedIdentityKey(provider.getProperty("signature.claimedIdentityKey"))
            .withStepUpLanguage(provider.getProperty("signature.stepUp.language"))
            .withStepUpMsisdn(provider.getProperty("signature.stepUp.msisdn"))
            .withStepUpMessage(provider.getProperty("signature.stepUp.message"))
            .withStepUpSerialNumber(provider.getProperty("signature.stepUp.serialNumber"))
            .withDistinguishedName(extractStringProperty(provider, "signature.distinguishedName"))
            .withSignatureName(provider.getProperty("signature.name"))
            .withSignatureReason(provider.getProperty("signature.reason"))
            .withSignatureLocation(provider.getProperty("signature.location"))
            .withSignatureContactInfo(provider.getProperty("signature.contactInfo"))
            .withSignatureStandard(extractProperty(provider, "signature.standard", SignatureStandard::getByValue, SignatureStandard.DEFAULT))
            .withRevocationInformation(extractProperty(provider, "signature.revocationInformation", RevocationInformation::getByValue,
                                                       RevocationInformation.DEFAULT))
            .withTimestamp(extractProperty(provider, "signature.addTimestamp", Boolean::parseBoolean, Boolean.TRUE));
    }

    public void validatePropertiesForSignature(SignatureMode signatureMode, Trace trace) {
        if (StringUtils.isBlank(transactionId)) {
            throw new AisClientException(String.format("The user data's transactionId cannot be null or empty. For example, you can set it to a new "
                                                       + "UUID or to any other value that is unique between requests. This helps with traceability "
                                                       + "in the logs generated by the AIS client. - %s", trace.getId()));
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
                throw new IllegalArgumentException(String.format("Invalid signature mode: %s - %s", signatureMode, trace.getId()));
        }
    }

    public static class Builder {
        private String transactionId = IdGenerator.generateId();

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
        private RevocationInformation revocationInformation = RevocationInformation.DEFAULT;
        private SignatureStandard signatureStandard = SignatureStandard.DEFAULT;

        Builder() {
        }

        public Builder withTransactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder withClaimedIdentityName(String claimedIdentityName) {
            this.claimedIdentityName = claimedIdentityName;
            return this;
        }

        public Builder withStepUpLanguage(String stepUpLanguage) {
            this.stepUpLanguage = stepUpLanguage;
            return this;
        }

        public Builder withStepUpMsisdn(String stepUpMsisdn) {
            this.stepUpMsisdn = stepUpMsisdn;
            return this;
        }

        public Builder withStepUpMessage(String stepUpMessage) {
            this.stepUpMessage = stepUpMessage;
            return this;
        }

        public Builder withDistinguishedName(String distinguishedName) {
            this.distinguishedName = distinguishedName;
            return this;
        }

        public Builder withConsentUrlCallback(ConsentUrlCallback consentUrlCallback) {
            this.consentUrlCallback = consentUrlCallback;
            return this;
        }

        public Builder withTimestamp(boolean addTimestamp) {
            this.addTimestamp = addTimestamp;
            return this;
        }

        public Builder withRevocationInformation(RevocationInformation revocationInformation) {
            this.revocationInformation = revocationInformation;
            return this;
        }

        public Builder withSignatureStandard(SignatureStandard signatureStandard) {
            this.signatureStandard = signatureStandard;
            return this;
        }

        public Builder withClaimedIdentityKey(String claimedIdentityKey) {
            this.claimedIdentityKey = claimedIdentityKey;
            return this;
        }

        public Builder withStepUpSerialNumber(String stepUpSerialNumber) {
            this.stepUpSerialNumber = stepUpSerialNumber;
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

        public UserData build() {
            return new UserData(transactionId, claimedIdentityName, claimedIdentityKey, distinguishedName, stepUpLanguage, stepUpMsisdn,
                                stepUpMessage, stepUpSerialNumber, signatureName, signatureReason, signatureLocation,
                                signatureContactInfo, consentUrlCallback, addTimestamp, revocationInformation, signatureStandard);
        }

        @Override
        public String toString() {
            return "UserData.Builder{" +
                   "transactionId='" + transactionId + '\'' +
                   ", claimedIdentityName='" + claimedIdentityName + '\'' +
                   ", claimedIdentityKey='" + claimedIdentityKey + '\'' +
                   ", distinguishedName='" + distinguishedName + '\'' +
                   ", stepUpLanguage='" + stepUpLanguage + '\'' +
                   ", stepUpMsisdn='" + stepUpMsisdn + '\'' +
                   ", stepUpMessage='" + stepUpMessage + '\'' +
                   ", stepUpSerialNumber='" + stepUpSerialNumber + '\'' +
                   ", signatureName='" + signatureName + '\'' +
                   ", signatureReason='" + signatureReason + '\'' +
                   ", signatureLocation='" + signatureLocation + '\'' +
                   ", signatureContactInfo='" + signatureContactInfo + '\'' +
                   ", consentUrlCallback=" + consentUrlCallback +
                   ", addTimestamp=" + addTimestamp +
                   ", revocationInformation=" + revocationInformation +
                   ", signatureStandard=" + signatureStandard +
                   '}';
        }
    }
}
