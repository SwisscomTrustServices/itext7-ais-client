package com.swisscom.ais.itext.client.model;

import java.util.Arrays;
import java.util.Optional;

public enum SignatureType {

    SIGN("sign"), TIMESTAMP("timestamp"), STATIC("static"), ON_DEMAND("ondemand"), ON_DEMAND_WITH_STEP_UP("ondemand-stepup");

    private final String type;

    private SignatureType(String type) {
        this.type = type;
    }

    public static Optional<SignatureType> getByTypeValue(String typeValue) {
        return Arrays.stream(values()).filter(v -> v.getType().equals(typeValue)).findFirst();
    }

    public String getType() {
        return type;
    }

}
