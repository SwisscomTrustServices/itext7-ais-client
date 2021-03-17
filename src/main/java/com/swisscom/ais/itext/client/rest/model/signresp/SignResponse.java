package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "@RequestID",
    "@Profile",
    "Result",
    "OptionalOutputs",
    "SignatureObject"
})
public class SignResponse {

    @JsonProperty("@RequestID")
    private String requestID;
    @JsonProperty("@Profile")
    private String profile;
    @JsonProperty("Result")
    private Result result;
    @JsonProperty("OptionalOutputs")
    private OptionalOutputs optionalOutputs;
    @JsonProperty("SignatureObject")
    private SignatureObject signatureObject;

    @JsonProperty("@RequestID")
    public String getRequestID() {
        return requestID;
    }

    @JsonProperty("@RequestID")
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public SignResponse withRequestID(String requestID) {
        this.requestID = requestID;
        return this;
    }

    @JsonProperty("@Profile")
    public String getProfile() {
        return profile;
    }

    @JsonProperty("@Profile")
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public SignResponse withProfile(String profile) {
        this.profile = profile;
        return this;
    }

    @JsonProperty("Result")
    public Result getResult() {
        return result;
    }

    @JsonProperty("Result")
    public void setResult(Result result) {
        this.result = result;
    }

    public SignResponse withResult(Result result) {
        this.result = result;
        return this;
    }

    @JsonProperty("OptionalOutputs")
    public OptionalOutputs getOptionalOutputs() {
        return optionalOutputs;
    }

    @JsonProperty("OptionalOutputs")
    public void setOptionalOutputs(OptionalOutputs optionalOutputs) {
        this.optionalOutputs = optionalOutputs;
    }

    public SignResponse withOptionalOutputs(OptionalOutputs optionalOutputs) {
        this.optionalOutputs = optionalOutputs;
        return this;
    }

    @JsonProperty("SignatureObject")
    public SignatureObject getSignatureObject() {
        return signatureObject;
    }

    @JsonProperty("SignatureObject")
    public void setSignatureObject(SignatureObject signatureObject) {
        this.signatureObject = signatureObject;
    }

    public SignResponse withSignatureObject(SignatureObject signatureObject) {
        this.signatureObject = signatureObject;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SignResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("requestID");
        sb.append('=');
        sb.append(((this.requestID == null) ? "<null>" : this.requestID));
        sb.append(',');
        sb.append("profile");
        sb.append('=');
        sb.append(((this.profile == null) ? "<null>" : this.profile));
        sb.append(',');
        sb.append("result");
        sb.append('=');
        sb.append(((this.result == null) ? "<null>" : this.result));
        sb.append(',');
        sb.append("optionalOutputs");
        sb.append('=');
        sb.append(((this.optionalOutputs == null) ? "<null>" : this.optionalOutputs));
        sb.append(',');
        sb.append("signatureObject");
        sb.append('=');
        sb.append(((this.signatureObject == null) ? "<null>" : this.signatureObject));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
