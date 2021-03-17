package com.swisscom.ais.itext.client.rest.model;

import java.util.Arrays;

public enum ResultMessageCode implements DocumentedEnum {

    INVALID_PASSWORD("urn:swisscom:names:sas:1.0:status:InvalidPassword",
                     "User entered an invalid password, as part of the Step Up phase"),
    INVALID_OTP("urn:swisscom:names:sas:1.0:status:InvalidOtp",
                "User entered an invalid OTP, as part of the Step Up phase");

    private final String uri;
    private final String description;

    ResultMessageCode(String uri, String description) {
        this.uri = uri;
        this.description = description;
    }

    public static ResultMessageCode getByUri(String uri) {
        return Arrays
            .stream(values())
            .filter(value -> value.getUri().equals(uri))
            .findFirst()
            .orElse(null);
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
