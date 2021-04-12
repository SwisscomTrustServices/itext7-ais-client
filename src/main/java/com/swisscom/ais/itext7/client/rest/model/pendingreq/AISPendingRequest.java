/*
 * Copyright 2021 Swisscom (Schweiz) AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swisscom.ais.itext7.client.rest.model.pendingreq;

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
