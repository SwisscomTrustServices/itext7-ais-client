package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SignResponse"
})
public class AISSignResponse {

    @JsonProperty("SignResponse")
    private SignResponse signResponse;

    @JsonProperty("SignResponse")
    public SignResponse getSignResponse() {
        return signResponse;
    }

    @JsonProperty("SignResponse")
    public void setSignResponse(SignResponse signResponse) {
        this.signResponse = signResponse;
    }

    public AISSignResponse withSignResponse(SignResponse signResponse) {
        this.signResponse = signResponse;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AISSignResponse.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("signResponse");
        sb.append('=');
        sb.append(((this.signResponse == null) ? "<null>" : this.signResponse));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
