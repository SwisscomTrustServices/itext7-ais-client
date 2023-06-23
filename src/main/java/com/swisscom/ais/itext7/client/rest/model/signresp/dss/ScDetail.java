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
    "ns1.detail",
    "ns2.UserAssistance"
})
public class ScDetail {

    @JsonProperty("ns1.detail")
    private String ns1Detail;
    @JsonProperty("ns2.UserAssistance")
    private Ns2UserAssistance ns2UserAssistance;

    @JsonProperty("ns1.detail")
    public String getNs1Detail() {
        return ns1Detail;
    }

    @JsonProperty("ns1.detail")
    public void setNs1Detail(String ns1Detail) {
        this.ns1Detail = ns1Detail;
    }

    public ScDetail withNs1Detail(String ns1Detail) {
        this.ns1Detail = ns1Detail;
        return this;
    }

    @JsonProperty("ns2.UserAssistance")
    public Ns2UserAssistance getNs2UserAssistance() {
        return ns2UserAssistance;
    }

    @JsonProperty("ns2.UserAssistance")
    public void setNs2UserAssistance(Ns2UserAssistance ns2UserAssistance) {
        this.ns2UserAssistance = ns2UserAssistance;
    }

    public ScDetail withNs2UserAssistance(Ns2UserAssistance ns2UserAssistance) {
        this.ns2UserAssistance = ns2UserAssistance;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScDetail.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("ns1Detail");
        sb.append('=');
        sb.append(((this.ns1Detail == null) ? "<null>" : this.ns1Detail));
        sb.append(',');
        sb.append("ns2UserAssistance");
        sb.append('=');
        sb.append(((this.ns2UserAssistance == null) ? "<null>" : this.ns2UserAssistance));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
