package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "$",
    "@Type"
})
public class Base64Signature {

    @JsonProperty("$")
    private String $;
    @JsonProperty("@Type")
    private String type;

    @JsonProperty("$")
    public String get$() {
        return $;
    }

    @JsonProperty("$")
    public void set$(String $) {
        this.$ = $;
    }

    public Base64Signature with$(String $) {
        this.$ = $;
        return this;
    }

    @JsonProperty("@Type")
    public String getType() {
        return type;
    }

    @JsonProperty("@Type")
    public void setType(String type) {
        this.type = type;
    }

    public Base64Signature withType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Base64Signature.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("$");
        sb.append('=');
        sb.append(((this.$ == null) ? "<null>" : this.$));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null) ? "<null>" : this.type));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
