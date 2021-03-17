package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "RFC3161TimeStampToken"
})
public class Timestamp {

    @JsonProperty("RFC3161TimeStampToken")
    private String rFC3161TimeStampToken;

    @JsonProperty("RFC3161TimeStampToken")
    public String getRFC3161TimeStampToken() {
        return rFC3161TimeStampToken;
    }

    @JsonProperty("RFC3161TimeStampToken")
    public void setRFC3161TimeStampToken(String rFC3161TimeStampToken) {
        this.rFC3161TimeStampToken = rFC3161TimeStampToken;
    }

    public Timestamp withRFC3161TimeStampToken(String rFC3161TimeStampToken) {
        this.rFC3161TimeStampToken = rFC3161TimeStampToken;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Timestamp.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("rFC3161TimeStampToken");
        sb.append('=');
        sb.append(((this.rFC3161TimeStampToken == null) ? "<null>" : this.rFC3161TimeStampToken));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
