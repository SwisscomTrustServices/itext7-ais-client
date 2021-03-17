package com.swisscom.ais.itext.client.rest.model.signreq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "@Algorithm"
})
public class DsigDigestMethod {

    @JsonProperty("@Algorithm")
    private String algorithm;

    @JsonProperty("@Algorithm")
    public String getAlgorithm() {
        return algorithm;
    }

    @JsonProperty("@Algorithm")
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public DsigDigestMethod withAlgorithm(String algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DsigDigestMethod.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("algorithm");
        sb.append('=');
        sb.append(((this.algorithm == null) ? "<null>" : this.algorithm));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
