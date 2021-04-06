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
package com.swisscom.ais.itext.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "RFC3161TimeStampToken"
})
public class Timestamp__1 {

    @JsonProperty("RFC3161TimeStampToken")
    private String rFC3161TimeStampToken;

    @JsonProperty("RFC3161TimeStampToken")
    public String getRFC3161TimeStampToken() {
        return rFC3161TimeStampToken;
    }

    @JsonProperty("RFC3161TimeStampToken")
    public void setRFC3161TimeStampToken(String rFC3161TimeStampToken) {
        this.rFC3161TimeStampToken = rFC3161TimeStampToken;
    }

    public Timestamp__1 withRFC3161TimeStampToken(String rFC3161TimeStampToken) {
        this.rFC3161TimeStampToken = rFC3161TimeStampToken;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Timestamp__1.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("rFC3161TimeStampToken");
        sb.append('=');
        sb.append(((this.rFC3161TimeStampToken == null) ? "<null>" : this.rFC3161TimeStampToken));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
