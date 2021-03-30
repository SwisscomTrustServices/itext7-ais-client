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
