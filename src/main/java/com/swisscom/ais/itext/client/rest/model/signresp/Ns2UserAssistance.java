package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ns2.PortalUrl"
})
public class Ns2UserAssistance {

    @JsonProperty("ns2.PortalUrl")
    private String ns2PortalUrl;

    @JsonProperty("ns2.PortalUrl")
    public String getNs2PortalUrl() {
        return ns2PortalUrl;
    }

    @JsonProperty("ns2.PortalUrl")
    public void setNs2PortalUrl(String ns2PortalUrl) {
        this.ns2PortalUrl = ns2PortalUrl;
    }

    public Ns2UserAssistance withNs2PortalUrl(String ns2PortalUrl) {
        this.ns2PortalUrl = ns2PortalUrl;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Ns2UserAssistance.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("ns2PortalUrl");
        sb.append('=');
        sb.append(((this.ns2PortalUrl == null) ? "<null>" : this.ns2PortalUrl));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
