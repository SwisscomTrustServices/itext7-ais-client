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
package com.swisscom.ais.itext7.client.rest.model.signreq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "AddTimestamp",
    "AdditionalProfile",
    "ClaimedIdentity",
    "SignatureType",
    "sc.AddRevocationInformation",
    "sc.SignatureStandard",
    "sc.CertificateRequest"
})
public class OptionalInputs {

    @JsonProperty("AddTimestamp")
    private AddTimestamp addTimestamp;
    @JsonProperty("AdditionalProfile")
    private List<String> additionalProfile = new ArrayList<String>();
    @JsonProperty("ClaimedIdentity")
    private ClaimedIdentity claimedIdentity;
    @JsonProperty("SignatureType")
    private String signatureType;
    @JsonProperty("sc.AddRevocationInformation")
    private ScAddRevocationInformation scAddRevocationInformation;
    @JsonProperty("sc.SignatureStandard")
    private String scSignatureStandard;
    @JsonProperty("sc.CertificateRequest")
    private ScCertificateRequest scCertificateRequest;

    @JsonProperty("AddTimestamp")
    public AddTimestamp getAddTimestamp() {
        return addTimestamp;
    }

    @JsonProperty("AddTimestamp")
    public void setAddTimestamp(AddTimestamp addTimestamp) {
        this.addTimestamp = addTimestamp;
    }

    public OptionalInputs withAddTimestamp(AddTimestamp addTimestamp) {
        this.addTimestamp = addTimestamp;
        return this;
    }

    @JsonProperty("AdditionalProfile")
    public List<String> getAdditionalProfile() {
        return additionalProfile;
    }

    @JsonProperty("AdditionalProfile")
    public void setAdditionalProfile(List<String> additionalProfile) {
        this.additionalProfile = additionalProfile;
    }

    public OptionalInputs withAdditionalProfile(List<String> additionalProfile) {
        this.additionalProfile = additionalProfile;
        return this;
    }

    @JsonProperty("ClaimedIdentity")
    public ClaimedIdentity getClaimedIdentity() {
        return claimedIdentity;
    }

    @JsonProperty("ClaimedIdentity")
    public void setClaimedIdentity(ClaimedIdentity claimedIdentity) {
        this.claimedIdentity = claimedIdentity;
    }

    public OptionalInputs withClaimedIdentity(ClaimedIdentity claimedIdentity) {
        this.claimedIdentity = claimedIdentity;
        return this;
    }

    @JsonProperty("SignatureType")
    public String getSignatureType() {
        return signatureType;
    }

    @JsonProperty("SignatureType")
    public void setSignatureType(String signatureType) {
        this.signatureType = signatureType;
    }

    public OptionalInputs withSignatureType(String signatureType) {
        this.signatureType = signatureType;
        return this;
    }

    @JsonProperty("sc.AddRevocationInformation")
    public ScAddRevocationInformation getScAddRevocationInformation() {
        return scAddRevocationInformation;
    }

    @JsonProperty("sc.AddRevocationInformation")
    public void setScAddRevocationInformation(ScAddRevocationInformation scAddRevocationInformation) {
        this.scAddRevocationInformation = scAddRevocationInformation;
    }

    public OptionalInputs withScAddRevocationInformation(ScAddRevocationInformation scAddRevocationInformation) {
        this.scAddRevocationInformation = scAddRevocationInformation;
        return this;
    }

    @JsonProperty("sc.SignatureStandard")
    public String getScSignatureStandard() {
        return scSignatureStandard;
    }

    @JsonProperty("sc.SignatureStandard")
    public void setScSignatureStandard(String scSignatureStandard) {
        this.scSignatureStandard = scSignatureStandard;
    }

    public OptionalInputs withScSignatureStandard(String scSignatureStandard) {
        this.scSignatureStandard = scSignatureStandard;
        return this;
    }

    @JsonProperty("sc.CertificateRequest")
    public ScCertificateRequest getScCertificateRequest() {
        return scCertificateRequest;
    }

    @JsonProperty("sc.CertificateRequest")
    public void setScCertificateRequest(ScCertificateRequest scCertificateRequest) {
        this.scCertificateRequest = scCertificateRequest;
    }

    public OptionalInputs withScCertificateRequest(ScCertificateRequest scCertificateRequest) {
        this.scCertificateRequest = scCertificateRequest;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(OptionalInputs.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("addTimestamp");
        sb.append('=');
        sb.append(((this.addTimestamp == null) ? "<null>" : this.addTimestamp));
        sb.append(',');
        sb.append("additionalProfile");
        sb.append('=');
        sb.append(((this.additionalProfile == null) ? "<null>" : this.additionalProfile));
        sb.append(',');
        sb.append("claimedIdentity");
        sb.append('=');
        sb.append(((this.claimedIdentity == null) ? "<null>" : this.claimedIdentity));
        sb.append(',');
        sb.append("signatureType");
        sb.append('=');
        sb.append(((this.signatureType == null) ? "<null>" : this.signatureType));
        sb.append(',');
        sb.append("scAddRevocationInformation");
        sb.append('=');
        sb.append(((this.scAddRevocationInformation == null) ? "<null>" : this.scAddRevocationInformation));
        sb.append(',');
        sb.append("scSignatureStandard");
        sb.append('=');
        sb.append(((this.scSignatureStandard == null) ? "<null>" : this.scSignatureStandard));
        sb.append(',');
        sb.append("scCertificateRequest");
        sb.append('=');
        sb.append(((this.scCertificateRequest == null) ? "<null>" : this.scCertificateRequest));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
