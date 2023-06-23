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
    "sc.SerialNumber",
    "sc.ConsentURL",
    "sc.MobileIDFault"
})
public class ScResult {

    @JsonProperty("sc.SerialNumber")
    private String scSerialNumber;
    @JsonProperty("sc.ConsentURL")
    private String scConsentURL;
    @JsonProperty("sc.MobileIDFault")
    private ScMobileIDFault scMobileIDFault;

    @JsonProperty("sc.SerialNumber")
    public String getScSerialNumber() {
        return scSerialNumber;
    }

    @JsonProperty("sc.SerialNumber")
    public void setScSerialNumber(String scSerialNumber) {
        this.scSerialNumber = scSerialNumber;
    }

    public ScResult withScSerialNumber(String scSerialNumber) {
        this.scSerialNumber = scSerialNumber;
        return this;
    }

    @JsonProperty("sc.ConsentURL")
    public String getScConsentURL() {
        return scConsentURL;
    }

    @JsonProperty("sc.ConsentURL")
    public void setScConsentURL(String scConsentURL) {
        this.scConsentURL = scConsentURL;
    }

    public ScResult withScConsentURL(String scConsentURL) {
        this.scConsentURL = scConsentURL;
        return this;
    }

    @JsonProperty("sc.MobileIDFault")
    public ScMobileIDFault getScMobileIDFault() {
        return scMobileIDFault;
    }

    @JsonProperty("sc.MobileIDFault")
    public void setScMobileIDFault(ScMobileIDFault scMobileIDFault) {
        this.scMobileIDFault = scMobileIDFault;
    }

    public ScResult withScMobileIDFault(ScMobileIDFault scMobileIDFault) {
        this.scMobileIDFault = scMobileIDFault;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScResult.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scSerialNumber");
        sb.append('=');
        sb.append(((this.scSerialNumber == null) ? "<null>" : this.scSerialNumber));
        sb.append(',');
        sb.append("scConsentURL");
        sb.append('=');
        sb.append(((this.scConsentURL == null) ? "<null>" : this.scConsentURL));
        sb.append(',');
        sb.append("scMobileIDFault");
        sb.append('=');
        sb.append(((this.scMobileIDFault == null) ? "<null>" : this.scMobileIDFault));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
