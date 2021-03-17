package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "async.ResponseID",
    "sc.APTransID",
    "sc.StepUpAuthorisationInfo",
    "sc.RevocationInformation"
})
public class OptionalOutputs {

    @JsonProperty("async.ResponseID")
    private String asyncResponseID;
    @JsonProperty("sc.APTransID")
    private String scAPTransID;
    @JsonProperty("sc.StepUpAuthorisationInfo")
    private ScStepUpAuthorisationInfo scStepUpAuthorisationInfo;
    @JsonProperty("sc.RevocationInformation")
    private ScRevocationInformation scRevocationInformation;

    @JsonProperty("async.ResponseID")
    public String getAsyncResponseID() {
        return asyncResponseID;
    }

    @JsonProperty("async.ResponseID")
    public void setAsyncResponseID(String asyncResponseID) {
        this.asyncResponseID = asyncResponseID;
    }

    public OptionalOutputs withAsyncResponseID(String asyncResponseID) {
        this.asyncResponseID = asyncResponseID;
        return this;
    }

    @JsonProperty("sc.APTransID")
    public String getScAPTransID() {
        return scAPTransID;
    }

    @JsonProperty("sc.APTransID")
    public void setScAPTransID(String scAPTransID) {
        this.scAPTransID = scAPTransID;
    }

    public OptionalOutputs withScAPTransID(String scAPTransID) {
        this.scAPTransID = scAPTransID;
        return this;
    }

    @JsonProperty("sc.StepUpAuthorisationInfo")
    public ScStepUpAuthorisationInfo getScStepUpAuthorisationInfo() {
        return scStepUpAuthorisationInfo;
    }

    @JsonProperty("sc.StepUpAuthorisationInfo")
    public void setScStepUpAuthorisationInfo(ScStepUpAuthorisationInfo scStepUpAuthorisationInfo) {
        this.scStepUpAuthorisationInfo = scStepUpAuthorisationInfo;
    }

    public OptionalOutputs withScStepUpAuthorisationInfo(ScStepUpAuthorisationInfo scStepUpAuthorisationInfo) {
        this.scStepUpAuthorisationInfo = scStepUpAuthorisationInfo;
        return this;
    }

    @JsonProperty("sc.RevocationInformation")
    public ScRevocationInformation getScRevocationInformation() {
        return scRevocationInformation;
    }

    @JsonProperty("sc.RevocationInformation")
    public void setScRevocationInformation(ScRevocationInformation scRevocationInformation) {
        this.scRevocationInformation = scRevocationInformation;
    }

    public OptionalOutputs withScRevocationInformation(ScRevocationInformation scRevocationInformation) {
        this.scRevocationInformation = scRevocationInformation;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(OptionalOutputs.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("asyncResponseID");
        sb.append('=');
        sb.append(((this.asyncResponseID == null) ? "<null>" : this.asyncResponseID));
        sb.append(',');
        sb.append("scAPTransID");
        sb.append('=');
        sb.append(((this.scAPTransID == null) ? "<null>" : this.scAPTransID));
        sb.append(',');
        sb.append("scStepUpAuthorisationInfo");
        sb.append('=');
        sb.append(((this.scStepUpAuthorisationInfo == null) ? "<null>" : this.scStepUpAuthorisationInfo));
        sb.append(',');
        sb.append("scRevocationInformation");
        sb.append('=');
        sb.append(((this.scRevocationInformation == null) ? "<null>" : this.scRevocationInformation));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
