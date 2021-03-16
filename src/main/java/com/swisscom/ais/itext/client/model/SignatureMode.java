package com.swisscom.ais.itext.client.model;

import java.util.Arrays;

public enum SignatureMode {

    TIMESTAMP("timestamp"), STATIC("static"), ON_DEMAND("ondemand"), ON_DEMAND_WITH_STEP_UP("ondemand-stepup");

    private final String value;

    SignatureMode(String value) {
        this.value = value;
    }

    public static SignatureMode getByValue(String signatureValue) {
        return Arrays.stream(values())
            .filter(v -> v.getValue().equalsIgnoreCase(signatureValue))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid signature value provided: %s", signatureValue)));
    }

    public String getValue() {
        return value;
    }

}
