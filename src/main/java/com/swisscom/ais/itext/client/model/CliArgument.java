package com.swisscom.ais.itext.client.model;

import java.util.Arrays;

public enum CliArgument {
    INIT("init"), INPUT("input"), OUTPUT("output"), SUFFIX("suffix"), CONFIG("config"), HELP("help"),
    SIGNATURE_TYPE("type"), BASIC_VERBOSITY("v"), MEDIUM_VERBOSITY("vv"), HIGH_VERBOSITY("vvv");

    private final String value;

    CliArgument(String value) {
        this.value = value;
    }

    public static CliArgument getByValue(String argValue) {
        return Arrays.stream(values())
            .filter(v -> v.getValue().equalsIgnoreCase(argValue))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Invalid CLI argument value provided: %s", argValue)));
    }

    public String getValue() {
        return value;
    }
}
