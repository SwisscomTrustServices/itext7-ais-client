package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sc.SerialNumber",
    "sc.ConsentURL",
    "sc.MobileIDFault"
})
public class ScResult {

    @JsonProperty("sc.SerialNumber")
    private String scSerialNumber;
    @JsonProperty("sc.ConsentURL")
    private String scConsentURL;
    @JsonProperty("sc.MobileIDFault")
    private ScMobileIDFault scMobileIDFault;

    @JsonProperty("sc.SerialNumber")
    public String getScSerialNumber() {
        return scSerialNumber;
    }

    @JsonProperty("sc.SerialNumber")
    public void setScSerialNumber(String scSerialNumber) {
        this.scSerialNumber = scSerialNumber;
    }

    public ScResult withScSerialNumber(String scSerialNumber) {
        this.scSerialNumber = scSerialNumber;
        return this;
    }

    @JsonProperty("sc.ConsentURL")
    public String getScConsentURL() {
        return scConsentURL;
    }

    @JsonProperty("sc.ConsentURL")
    public void setScConsentURL(String scConsentURL) {
        this.scConsentURL = scConsentURL;
    }

    public ScResult withScConsentURL(String scConsentURL) {
        this.scConsentURL = scConsentURL;
        return this;
    }

    @JsonProperty("sc.MobileIDFault")
    public ScMobileIDFault getScMobileIDFault() {
        return scMobileIDFault;
    }

    @JsonProperty("sc.MobileIDFault")
    public void setScMobileIDFault(ScMobileIDFault scMobileIDFault) {
        this.scMobileIDFault = scMobileIDFault;
    }

    public ScResult withScMobileIDFault(ScMobileIDFault scMobileIDFault) {
        this.scMobileIDFault = scMobileIDFault;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScResult.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scSerialNumber");
        sb.append('=');
        sb.append(((this.scSerialNumber == null) ? "<null>" : this.scSerialNumber));
        sb.append(',');
        sb.append("scConsentURL");
        sb.append('=');
        sb.append(((this.scConsentURL == null) ? "<null>" : this.scConsentURL));
        sb.append(',');
        sb.append("scMobileIDFault");
        sb.append('=');
        sb.append(((this.scMobileIDFault == null) ? "<null>" : this.scMobileIDFault));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
