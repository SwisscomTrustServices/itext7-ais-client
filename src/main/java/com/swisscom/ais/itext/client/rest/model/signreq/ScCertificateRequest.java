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
package com.swisscom.ais.itext.client.rest.model.signreq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sc.DistinguishedName",
    "sc.StepUpAuthorisation"
})
public class ScCertificateRequest {

    @JsonProperty("sc.DistinguishedName")
    private String scDistinguishedName;
    @JsonProperty("sc.StepUpAuthorisation")
    private ScStepUpAuthorisation scStepUpAuthorisation;

    @JsonProperty("sc.DistinguishedName")
    public String getScDistinguishedName() {
        return scDistinguishedName;
    }

    @JsonProperty("sc.DistinguishedName")
    public void setScDistinguishedName(String scDistinguishedName) {
        this.scDistinguishedName = scDistinguishedName;
    }

    public ScCertificateRequest withScDistinguishedName(String scDistinguishedName) {
        this.scDistinguishedName = scDistinguishedName;
        return this;
    }

    @JsonProperty("sc.StepUpAuthorisation")
    public ScStepUpAuthorisation getScStepUpAuthorisation() {
        return scStepUpAuthorisation;
    }

    @JsonProperty("sc.StepUpAuthorisation")
    public void setScStepUpAuthorisation(ScStepUpAuthorisation scStepUpAuthorisation) {
        this.scStepUpAuthorisation = scStepUpAuthorisation;
    }

    public ScCertificateRequest withScStepUpAuthorisation(ScStepUpAuthorisation scStepUpAuthorisation) {
        this.scStepUpAuthorisation = scStepUpAuthorisation;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScCertificateRequest.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scDistinguishedName");
        sb.append('=');
        sb.append(((this.scDistinguishedName == null) ? "<null>" : this.scDistinguishedName));
        sb.append(',');
        sb.append("scStepUpAuthorisation");
        sb.append('=');
        sb.append(((this.scStepUpAuthorisation == null) ? "<null>" : this.scStepUpAuthorisation));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
