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
package com.swisscom.ais.itext7.client.rest.model.signresp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "@xml.lang",
    "$"
})
public class ResultMessage {

    @JsonProperty("@xml.lang")
    private String xmlLang;
    @JsonProperty("$")
    private String $;

    @JsonProperty("@xml.lang")
    public String getXmlLang() {
        return xmlLang;
    }

    @JsonProperty("@xml.lang")
    public void setXmlLang(String xmlLang) {
        this.xmlLang = xmlLang;
    }

    public ResultMessage withXmlLang(String xmlLang) {
        this.xmlLang = xmlLang;
        return this;
    }

    @JsonProperty("$")
    public String get$() {
        return $;
    }

    @JsonProperty("$")
    public void set$(String $) {
        this.$ = $;
    }

    public ResultMessage with$(String $) {
        this.$ = $;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ResultMessage.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("xmlLang");
        sb.append('=');
        sb.append(((this.xmlLang == null) ? "<null>" : this.xmlLang));
        sb.append(',');
        sb.append("$");
        sb.append('=');
        sb.append(((this.$ == null) ? "<null>" : this.$));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
