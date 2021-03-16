package com.swisscom.ais.itext.client.model;

import java.util.Arrays;

public enum RevocationInformation {

    /**
     * For CMS signatures, if the attribute for revocation information is not provided (set as default),
     * the revocation information will match the defined SignatureStandard (CAdES or PAdES).
     * (In case the SignatureStandard is not set, the default is CAdES).
     *
     * For timestamps, the signature standard does not apply so the revocation information type must be
     * set explicitly.
     */
    DEFAULT(""),

    /**
     * Encode revocation information in CMS compliant to CAdES.
     * RI will be embedded as an unsigned attribute with OID 1.2.840.113549.1.9.16.2.24.
     */
    CADES("CAdES"),

    /**
     * PDF (formerly named PAdES): CMS Signatures: RI will be embedded in the signature as a signed attribute
     * with OID 1.2.840.113583.1.1.8. Trusted Timestamps: RI will be provided in the response as Base64 encoded
     * OCSP responses or CRLs within the <OptionalOutputs>-Element.
     */
    PDF("PDF"),

    /**
     * Alias for PDF for backward compatibility. Since 1st of December 2020, the PADES signature standard has been replaced
     * with the PDF option, to better transmit the idea that the revocation information archival attribute is added to the
     * CMS signature that is returned to the client, as per the PDF reference. This revocation information value (PADES) is now
     * deprecated and should not be used. Use instead the PDF one, which has the same behaviour.
     *
     * @deprecated Please use the {@link #PDF} element.
     */
    @Deprecated
    PADES("PAdES"),

    /**
     * Add optional output with revocation information, to be used by clients to create PAdES-compliant signatures.
     * In order to get an LTV-enabled PDF signature, the client must process the optional output and fill the PDF's DSS (this AIS client
     * library already does this for your). This is in contrast with the PDF option (see above) that embeds the revocation information
     * as an archival attribute inside the CMS content, which might trip some strict checkers (e.g. ETSI Signature Conformance Checker).
     */
    PADES_BASELINE("PAdES-Baseline"),

    /**
     * Both RI types (CAdES and PDF) will be provided (for backward compatibility).
     */
    BOTH("BOTH"),

    /**
     * Add optional output with revocation information.
     */
    PLAIN("PLAIN");

    private final String value;

    RevocationInformation(String value) {
        this.value = value;
    }

    public static RevocationInformation getByValue(String value) {
        return Arrays.stream(values())
            .filter(item -> item.getValue().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Invalid revocation information value: " + value));
    }

    public String getValue() {
        return value;
    }
}
