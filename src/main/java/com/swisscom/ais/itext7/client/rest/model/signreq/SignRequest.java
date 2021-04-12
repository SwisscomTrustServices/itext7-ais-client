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
package com.swisscom.ais.itext7.client.rest.model.signreq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "@Profile",
    "@RequestID",
    "InputDocuments",
    "OptionalInputs"
})
public class SignRequest {

    @JsonProperty("@Profile")
    private String profile;
    @JsonProperty("@RequestID")
    private String requestID;
    @JsonProperty("InputDocuments")
    private InputDocuments inputDocuments;
    @JsonProperty("OptionalInputs")
    private OptionalInputs optionalInputs;

    @JsonProperty("@Profile")
    public String getProfile() {
        return profile;
    }

    @JsonProperty("@Profile")
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public SignRequest withProfile(String profile) {
        this.profile = profile;
        return this;
    }

    @JsonProperty("@RequestID")
    public String getRequestID() {
        return requestID;
    }

    @JsonProperty("@RequestID")
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public SignRequest withRequestID(String requestID) {
        this.requestID = requestID;
        return this;
    }

    @JsonProperty("InputDocuments")
    public InputDocuments getInputDocuments() {
        return inputDocuments;
    }

    @JsonProperty("InputDocuments")
    public void setInputDocuments(InputDocuments inputDocuments) {
        this.inputDocuments = inputDocuments;
    }

    public SignRequest withInputDocuments(InputDocuments inputDocuments) {
        this.inputDocuments = inputDocuments;
        return this;
    }

    @JsonProperty("OptionalInputs")
    public OptionalInputs getOptionalInputs() {
        return optionalInputs;
    }

    @JsonProperty("OptionalInputs")
    public void setOptionalInputs(OptionalInputs optionalInputs) {
        this.optionalInputs = optionalInputs;
    }

    public SignRequest withOptionalInputs(OptionalInputs optionalInputs) {
        this.optionalInputs = optionalInputs;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SignRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("profile");
        sb.append('=');
        sb.append(((this.profile == null) ? "<null>" : this.profile));
        sb.append(',');
        sb.append("requestID");
        sb.append('=');
        sb.append(((this.requestID == null) ? "<null>" : this.requestID));
        sb.append(',');
        sb.append("inputDocuments");
        sb.append('=');
        sb.append(((this.inputDocuments == null) ? "<null>" : this.inputDocuments));
        sb.append(',');
        sb.append("optionalInputs");
        sb.append('=');
        sb.append(((this.optionalInputs == null) ? "<null>" : this.optionalInputs));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
