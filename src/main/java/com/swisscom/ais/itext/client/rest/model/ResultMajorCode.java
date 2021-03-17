package com.swisscom.ais.itext.client.rest.model;

import java.util.Arrays;

public enum ResultMajorCode implements DocumentedEnum {

    SUBSYSTEM_ERROR("http://ais.swisscom.ch/1.0/resultmajor/SubsystemError",
                    "Some subsystem of the server produced an error. Details are included in the minor status code."),
    PENDING("urn:oasis:names:tc:dss:1.0:profiles:asynchronousprocessing:resultmajor:Pending",
            "Asynchronous request was accepted. It is pending now."),
    REQUESTER_ERROR("urn:oasis:names:tc:dss:1.0:resultmajor:RequesterError",
                    "It is assumed that the caller has made a mistake. Details are included in the minor status code."),
    RESPONDER_ERROR("urn:oasis:names:tc:dss:1.0:resultmajor:ResponderError",
                    "The server could not process the request. Details are included in the minor status code."),
    SUCCESS("urn:oasis:names:tc:dss:1.0:resultmajor:Success",
            "Request was successfully executed");

    private final String uri;
    private final String description;

    ResultMajorCode(String uri, String description) {
        this.uri = uri;
        this.description = description;
    }

    public static ResultMajorCode getByUri(String uri) {
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
