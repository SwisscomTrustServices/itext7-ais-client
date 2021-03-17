package com.swisscom.ais.itext.client.rest.model.pendingreq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ClaimedIdentity",
    "async.ResponseID"
})
public class OptionalInputs {

    @JsonProperty("ClaimedIdentity")
    private ClaimedIdentity claimedIdentity;
    @JsonProperty("async.ResponseID")
    private String asyncResponseID;

    @JsonProperty("ClaimedIdentity")
    public ClaimedIdentity getClaimedIdentity() {
        return claimedIdentity;
    }

    @JsonProperty("ClaimedIdentity")
    public void setClaimedIdentity(ClaimedIdentity claimedIdentity) {
        this.claimedIdentity = claimedIdentity;
    }

    public OptionalInputs withClaimedIdentity(ClaimedIdentity claimedIdentity) {
        this.claimedIdentity = claimedIdentity;
        return this;
    }

    @JsonProperty("async.ResponseID")
    public String getAsyncResponseID() {
        return asyncResponseID;
    }

    @JsonProperty("async.ResponseID")
    public void setAsyncResponseID(String asyncResponseID) {
        this.asyncResponseID = asyncResponseID;
    }

    public OptionalInputs withAsyncResponseID(String asyncResponseID) {
        this.asyncResponseID = asyncResponseID;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(OptionalInputs.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("claimedIdentity");
        sb.append('=');
        sb.append(((this.claimedIdentity == null) ? "<null>" : this.claimedIdentity));
        sb.append(',');
        sb.append("asyncResponseID");
        sb.append('=');
        sb.append(((this.asyncResponseID == null) ? "<null>" : this.asyncResponseID));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
