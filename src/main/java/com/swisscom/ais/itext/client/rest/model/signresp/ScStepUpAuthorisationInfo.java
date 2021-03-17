package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sc.Result"
})
public class ScStepUpAuthorisationInfo {

    @JsonProperty("sc.Result")
    private ScResult scResult;

    @JsonProperty("sc.Result")
    public ScResult getScResult() {
        return scResult;
    }

    @JsonProperty("sc.Result")
    public void setScResult(ScResult scResult) {
        this.scResult = scResult;
    }

    public ScStepUpAuthorisationInfo withScResult(ScResult scResult) {
        this.scResult = scResult;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScStepUpAuthorisationInfo.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scResult");
        sb.append('=');
        sb.append(((this.scResult == null) ? "<null>" : this.scResult));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
