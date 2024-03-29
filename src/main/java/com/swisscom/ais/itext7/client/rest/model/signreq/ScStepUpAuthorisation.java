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
    "sc.Phone"
})
public class ScStepUpAuthorisation {

    @JsonProperty("sc.Phone")
    private ScPhone scPhone;

    @JsonProperty("sc.Phone")
    public ScPhone getScPhone() {
        return scPhone;
    }

    @JsonProperty("sc.Phone")
    public void setScPhone(ScPhone scPhone) {
        this.scPhone = scPhone;
    }

    public ScStepUpAuthorisation withScPhone(ScPhone scPhone) {
        this.scPhone = scPhone;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScStepUpAuthorisation.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scPhone");
        sb.append('=');
        sb.append(((this.scPhone == null) ? "<null>" : this.scPhone));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
