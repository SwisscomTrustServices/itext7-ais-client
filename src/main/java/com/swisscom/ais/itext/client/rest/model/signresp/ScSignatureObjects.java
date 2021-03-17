package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sc.ExtendedSignatureObject"
})
public class ScSignatureObjects {

    @JsonProperty("sc.ExtendedSignatureObject")
    private List<ScExtendedSignatureObject> scExtendedSignatureObject = new ArrayList<ScExtendedSignatureObject>();

    @JsonProperty("sc.ExtendedSignatureObject")
    public List<ScExtendedSignatureObject> getScExtendedSignatureObject() {
        return scExtendedSignatureObject;
    }

    @JsonProperty("sc.ExtendedSignatureObject")
    public void setScExtendedSignatureObject(List<ScExtendedSignatureObject> scExtendedSignatureObject) {
        this.scExtendedSignatureObject = scExtendedSignatureObject;
    }

    public ScSignatureObjects withScExtendedSignatureObject(List<ScExtendedSignatureObject> scExtendedSignatureObject) {
        this.scExtendedSignatureObject = scExtendedSignatureObject;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScSignatureObjects.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scExtendedSignatureObject");
        sb.append('=');
        sb.append(((this.scExtendedSignatureObject == null) ? "<null>" : this.scExtendedSignatureObject));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
