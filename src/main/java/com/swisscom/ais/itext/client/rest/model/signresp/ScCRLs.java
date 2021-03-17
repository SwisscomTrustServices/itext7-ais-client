package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sc.CRL"
})
public class ScCRLs {

    @JsonProperty("sc.CRL")
    private List<String> scCRL = new ArrayList<String>();

    @JsonProperty("sc.CRL")
    public List<String> getScCRL() {
        return scCRL;
    }

    @JsonProperty("sc.CRL")
    public void setScCRL(List<String> scCRL) {
        this.scCRL = scCRL;
    }

    public ScCRLs withScCRL(List<String> scCRL) {
        this.scCRL = scCRL;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScCRLs.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scCRL");
        sb.append('=');
        sb.append(((this.scCRL == null) ? "<null>" : this.scCRL));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
