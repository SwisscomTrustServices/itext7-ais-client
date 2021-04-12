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
    "@Profile",
    "OptionalInputs"
})
public class AsyncPendingRequest {

    @JsonProperty("@Profile")
    private String profile;
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

    public AsyncPendingRequest withProfile(String profile) {
        this.profile = profile;
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

    public AsyncPendingRequest withOptionalInputs(OptionalInputs optionalInputs) {
        this.optionalInputs = optionalInputs;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AsyncPendingRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("profile");
        sb.append('=');
        sb.append(((this.profile == null) ? "<null>" : this.profile));
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
