package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ns1.detail",
    "ns2.UserAssistance"
})
public class ScDetail {

    @JsonProperty("ns1.detail")
    private String ns1Detail;
    @JsonProperty("ns2.UserAssistance")
    private Ns2UserAssistance ns2UserAssistance;

    @JsonProperty("ns1.detail")
    public String getNs1Detail() {
        return ns1Detail;
    }

    @JsonProperty("ns1.detail")
    public void setNs1Detail(String ns1Detail) {
        this.ns1Detail = ns1Detail;
    }

    public ScDetail withNs1Detail(String ns1Detail) {
        this.ns1Detail = ns1Detail;
        return this;
    }

    @JsonProperty("ns2.UserAssistance")
    public Ns2UserAssistance getNs2UserAssistance() {
        return ns2UserAssistance;
    }

    @JsonProperty("ns2.UserAssistance")
    public void setNs2UserAssistance(Ns2UserAssistance ns2UserAssistance) {
        this.ns2UserAssistance = ns2UserAssistance;
    }

    public ScDetail withNs2UserAssistance(Ns2UserAssistance ns2UserAssistance) {
        this.ns2UserAssistance = ns2UserAssistance;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScDetail.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("ns1Detail");
        sb.append('=');
        sb.append(((this.ns1Detail == null) ? "<null>" : this.ns1Detail));
        sb.append(',');
        sb.append("ns2UserAssistance");
        sb.append('=');
        sb.append(((this.ns2UserAssistance == null) ? "<null>" : this.ns2UserAssistance));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
