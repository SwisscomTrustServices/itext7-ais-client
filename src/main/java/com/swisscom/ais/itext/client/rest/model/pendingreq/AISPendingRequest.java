package com.swisscom.ais.itext.client.rest.model.pendingreq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "async.PendingRequest"
})
public class AISPendingRequest {

    @JsonProperty("async.PendingRequest")
    private AsyncPendingRequest asyncPendingRequest;

    @JsonProperty("async.PendingRequest")
    public AsyncPendingRequest getAsyncPendingRequest() {
        return asyncPendingRequest;
    }

    @JsonProperty("async.PendingRequest")
    public void setAsyncPendingRequest(AsyncPendingRequest asyncPendingRequest) {
        this.asyncPendingRequest = asyncPendingRequest;
    }

    public AISPendingRequest withAsyncPendingRequest(AsyncPendingRequest asyncPendingRequest) {
        this.asyncPendingRequest = asyncPendingRequest;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AISPendingRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("asyncPendingRequest");
        sb.append('=');
        sb.append(((this.asyncPendingRequest == null) ? "<null>" : this.asyncPendingRequest));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
