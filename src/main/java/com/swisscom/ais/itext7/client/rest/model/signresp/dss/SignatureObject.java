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
package com.swisscom.ais.itext7.client.rest.model.signresp.dss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Base64Signature",
    "Timestamp",
    "Other"
})
public class SignatureObject {

    @JsonProperty("Base64Signature")
    private Base64Signature base64Signature;
    @JsonProperty("Timestamp")
    private Timestamp timestamp;
    @JsonProperty("Other")
    private Other other;

    @JsonProperty("Base64Signature")
    public Base64Signature getBase64Signature() {
        return base64Signature;
    }

    @JsonProperty("Base64Signature")
    public void setBase64Signature(Base64Signature base64Signature) {
        this.base64Signature = base64Signature;
    }

    public SignatureObject withBase64Signature(Base64Signature base64Signature) {
        this.base64Signature = base64Signature;
        return this;
    }

    @JsonProperty("Timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @JsonProperty("Timestamp")
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public SignatureObject withTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    @JsonProperty("Other")
    public Other getOther() {
        return other;
    }

    @JsonProperty("Other")
    public void setOther(Other other) {
        this.other = other;
    }

    public SignatureObject withOther(Other other) {
        this.other = other;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SignatureObject.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("base64Signature");
        sb.append('=');
        sb.append(((this.base64Signature == null) ? "<null>" : this.base64Signature));
        sb.append(',');
        sb.append("timestamp");
        sb.append('=');
        sb.append(((this.timestamp == null) ? "<null>" : this.timestamp));
        sb.append(',');
        sb.append("other");
        sb.append('=');
        sb.append(((this.other == null) ? "<null>" : this.other));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
