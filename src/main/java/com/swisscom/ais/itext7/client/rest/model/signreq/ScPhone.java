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
    "sc.Language",
    "sc.MSISDN",
    "sc.Message",
    "sc.SerialNumber"
})
public class ScPhone {

    @JsonProperty("sc.Language")
    private String scLanguage;
    @JsonProperty("sc.MSISDN")
    private String scMSISDN;
    @JsonProperty("sc.Message")
    private String scMessage;
    @JsonProperty("sc.SerialNumber")
    private String scSerialNumber;

    @JsonProperty("sc.Language")
    public String getScLanguage() {
        return scLanguage;
    }

    @JsonProperty("sc.Language")
    public void setScLanguage(String scLanguage) {
        this.scLanguage = scLanguage;
    }

    public ScPhone withScLanguage(String scLanguage) {
        this.scLanguage = scLanguage;
        return this;
    }

    @JsonProperty("sc.MSISDN")
    public String getScMSISDN() {
        return scMSISDN;
    }

    @JsonProperty("sc.MSISDN")
    public void setScMSISDN(String scMSISDN) {
        this.scMSISDN = scMSISDN;
    }

    public ScPhone withScMSISDN(String scMSISDN) {
        this.scMSISDN = scMSISDN;
        return this;
    }

    @JsonProperty("sc.Message")
    public String getScMessage() {
        return scMessage;
    }

    @JsonProperty("sc.Message")
    public void setScMessage(String scMessage) {
        this.scMessage = scMessage;
    }

    public ScPhone withScMessage(String scMessage) {
        this.scMessage = scMessage;
        return this;
    }

    @JsonProperty("sc.SerialNumber")
    public String getScSerialNumber() {
        return scSerialNumber;
    }

    @JsonProperty("sc.SerialNumber")
    public void setScSerialNumber(String scSerialNumber) {
        this.scSerialNumber = scSerialNumber;
    }

    public ScPhone withScSerialNumber(String scSerialNumber) {
        this.scSerialNumber = scSerialNumber;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ScPhone.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("scLanguage");
        sb.append('=');
        sb.append(((this.scLanguage == null) ? "<null>" : this.scLanguage));
        sb.append(',');
        sb.append("scMSISDN");
        sb.append('=');
        sb.append(((this.scMSISDN == null) ? "<null>" : this.scMSISDN));
        sb.append(',');
        sb.append("scMessage");
        sb.append('=');
        sb.append(((this.scMessage == null) ? "<null>" : this.scMessage));
        sb.append(',');
        sb.append("scSerialNumber");
        sb.append('=');
        sb.append(((this.scSerialNumber == null) ? "<null>" : this.scSerialNumber));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
