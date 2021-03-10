package com.swisscom.ais.itext.client.model;

import java.util.Arrays;
import java.util.Optional;

public enum CliArgument {
    INIT("init"), INPUT("input"), OUTPUT("output"), SUFFIX("suffix"), CONFIG("config"), HELP("help"),
    SIGNATURE_TYPE("type"), DISTINGUISHED_NAME("dn"), STEP_UP_MSISDN("stepupmsisdn"), STEP_UP_MESSAGE("stepupmsg"),
    STEP_UP_LANGUAGE("stepuplang"), STEP_UP_SERIAL_NO("stepupserialnumber"), SIGNATURE_REASON("reason"),
    SIGNATURE_LOCATION("location"), SIGNATURE_CONTACT_INFO("contact"), CERTIFICATION_LEVEL("certlevel"),
    BASIC_VERBOSITY("v"), MEDIUM_VERBOSITY("vv"), HIGH_VERBOSITY("vvv");

    private final String argValue;

    private CliArgument(String argValue) {
        this.argValue = argValue;
    }

    public static Optional<CliArgument> getByArgumentValue(String argValue) {
        return Arrays.stream(values()).filter(v -> v.getArgValue().equals(argValue)).findFirst();
    }

    public String getArgValue() {
        return argValue;
    }
}
