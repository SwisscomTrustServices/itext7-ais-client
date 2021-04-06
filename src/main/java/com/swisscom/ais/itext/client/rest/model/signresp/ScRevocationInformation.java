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
    "sc.CRLs",
    "sc.OCSPs"
})
public class ScRevocationInformation {

    @JsonProperty("sc.CRLs")
    private ScCRLs scCRLs;
    @JsonProperty("sc.OCSPs")
    private ScOCSPs scOCSPs;

    @JsonProperty("sc.CRLs")
    public ScCRLs getScCRLs() {
        return scCRLs;
    }

    @JsonProperty("sc.CRLs")
    public void setScCRLs(ScCRLs scCRLs) {
        this.scCRLs = scCRLs;
    }

    public ScRevocationInformation withScCRLs(ScCRLs scCRLs) {
        this.scCRLs = scCRLs;
        return this;
    }

    @JsonProperty("sc.OCSPs")
    public ScOCSPs getScOCSPs() {
        return scOCSPs;
    }

    @JsonProperty("sc.OCSPs")
    public void setScOCSPs(ScOCSPs scOCSPs) {
        this.scOCSPs = scOCSPs;
    }

    public ScRevocationInformation withScOCSPs(ScOCSPs scOCSPs) {
        this.scOCSPs = scOCSPs;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScRevocationInformation.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scCRLs");
        sb.append('=');
        sb.append(((this.scCRLs == null) ? "<null>" : this.scCRLs));
        sb.append(',');
        sb.append("scOCSPs");
        sb.append('=');
        sb.append(((this.scOCSPs == null) ? "<null>" : this.scOCSPs));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
