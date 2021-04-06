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

import java.util.Arrays;

public enum SignatureStandard {

    /**
     * The default signature standard that is used. This is normally CAdES but the actual decision is left to the AIS service.
     */
    DEFAULT(""),

    /**
     * CAdES compliant signature.
     */
    CADES("CAdES"),

    /**
     * Formerly named PAdES: Adds to the CMS a revocation info archival attribute as described in the PDF reference.
     */
    PDF("PDF"),

    /**
     * Alias for PDF for backward compatibility. Since 1st of December 2020, the PADES signature standard has been replaced
     * with the PDF option, to better transmit the idea that the revocation information archival attribute is added to the
     * CMS signature that is returned to the client, as per the PDF reference. This signature standard (PADES) is now
     * deprecated and should not be used. Use instead the PDF one, which has the same behaviour.
     *
     * @deprecated Please use the {@link #PDF} element.
     */
    @Deprecated
    PADES("PAdES"),

    /**
     * PAdES compliant signature, which returns the revocation information as optional output.
     * In order to get an LTV-enabled PDF signature, the client must process the optional output and fill the PDF's DSS (this AIS client
     * library already does this for you). This is in contrast with the PDF option (see above) that embeds the revocation information
     * as an archival attribute inside the CMS content, which might trip some strict checkers (e.g. ETSI Signature Conformance Checker).
     */
    PADES_BASELINE("PAdES-baseline"),

    /**
     * Plain signature, which returns revocation information as optional output.
     */
    PLAIN("PLAIN");

    private final String value;

    SignatureStandard(String value) {
        this.value = value;
    }

    public static SignatureStandard getByValue(String value) {
        return Arrays.stream(values())
            .filter(item -> item.getValue().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid signature standard value provided: %s", value)));
    }

    public String getValue() {
        return value;
    }
}
