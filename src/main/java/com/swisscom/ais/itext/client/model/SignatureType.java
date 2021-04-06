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
package com.swisscom.ais.itext.client.model;

public enum SignatureType {

    /**
     * CMS signature (RFC 3369).
     */
    CMS("urn:ietf:rfc:3369", 30_000),

    /**
     * Timestamp signature (RFC 3161).
     */
    TIMESTAMP("urn:ietf:rfc:3161", 15_000);

    /**
     * URI of the signature type.
     */
    private final String uri;

    /**
     * The estimated final size of the signature in bytes.
     */
    private final int estimatedSignatureSizeInBytes;

    SignatureType(String uri, int estimatedSignatureSizeInBytes) {
        this.uri = uri;
        this.estimatedSignatureSizeInBytes = estimatedSignatureSizeInBytes;
    }

    public String getUri() {
        return uri;
    }

    public int getEstimatedSignatureSizeInBytes() {
        return estimatedSignatureSizeInBytes;
    }

}
