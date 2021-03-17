package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sc.SignatureObjects"
})
public class Other {

    @JsonProperty("sc.SignatureObjects")
    private ScSignatureObjects scSignatureObjects;

    @JsonProperty("sc.SignatureObjects")
    public ScSignatureObjects getScSignatureObjects() {
        return scSignatureObjects;
    }

    @JsonProperty("sc.SignatureObjects")
    public void setScSignatureObjects(ScSignatureObjects scSignatureObjects) {
        this.scSignatureObjects = scSignatureObjects;
    }

    public Other withScSignatureObjects(ScSignatureObjects scSignatureObjects) {
        this.scSignatureObjects = scSignatureObjects;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Other.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scSignatureObjects");
        sb.append('=');
        sb.append(((this.scSignatureObjects == null) ? "<null>" : this.scSignatureObjects));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
