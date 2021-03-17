package com.swisscom.ais.itext.client.rest.model.signreq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sc.Phone"
})
public class ScStepUpAuthorisation {

    @JsonProperty("sc.Phone")
    private ScPhone scPhone;

    @JsonProperty("sc.Phone")
    public ScPhone getScPhone() {
        return scPhone;
    }

    @JsonProperty("sc.Phone")
    public void setScPhone(ScPhone scPhone) {
        this.scPhone = scPhone;
    }

    public ScStepUpAuthorisation withScPhone(ScPhone scPhone) {
        this.scPhone = scPhone;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScStepUpAuthorisation.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scPhone");
        sb.append('=');
        sb.append(((this.scPhone == null) ? "<null>" : this.scPhone));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
