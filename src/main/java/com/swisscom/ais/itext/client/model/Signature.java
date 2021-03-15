package com.swisscom.ais.itext.client.model;

import java.util.Arrays;
import java.util.Optional;

public enum Signature {

    TIMESTAMP("timestamp"), STATIC("static"), ON_DEMAND("ondemand"), ON_DEMAND_WITH_STEP_UP("ondemand-stepup");

    private final String type;

    private Signature(String type) {
        this.type = type;
    }

    public static Optional<Signature> getByTypeValue(String typeValue) {
        return Arrays.stream(values()).filter(v -> v.getType().equals(typeValue)).findFirst();
    }

    public String getType() {
        return type;
    }

}
