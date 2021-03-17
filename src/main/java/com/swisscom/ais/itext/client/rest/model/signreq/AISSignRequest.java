package com.swisscom.ais.itext.client.rest.model.signreq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "SignRequest"
})
public class AISSignRequest {

    @JsonProperty("SignRequest")
    private SignRequest signRequest;

    @JsonProperty("SignRequest")
    public SignRequest getSignRequest() {
        return signRequest;
    }

    @JsonProperty("SignRequest")
    public void setSignRequest(SignRequest signRequest) {
        this.signRequest = signRequest;
    }

    public AISSignRequest withSignRequest(SignRequest signRequest) {
        this.signRequest = signRequest;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AISSignRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("signRequest");
        sb.append('=');
        sb.append(((this.signRequest == null) ? "<null>" : this.signRequest));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
