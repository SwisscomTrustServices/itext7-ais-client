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
package com.swisscom.ais.itext7.client.rest.model.signreq.dss;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "DocumentHash"
})
public class InputDocuments {

    @JsonProperty("DocumentHash")
    private List<DocumentHash> documentHash = new ArrayList<>();

    @JsonProperty("DocumentHash")
    public List<DocumentHash> getDocumentHash() {
        return documentHash;
    }

    @JsonProperty("DocumentHash")
    public void setDocumentHash(List<DocumentHash> documentHash) {
        this.documentHash = documentHash;
    }

    public InputDocuments withDocumentHash(List<DocumentHash> documentHash) {
        this.documentHash = documentHash;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(InputDocuments.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("documentHash");
        sb.append('=');
        sb.append(((this.documentHash == null) ? "<null>" : this.documentHash));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
