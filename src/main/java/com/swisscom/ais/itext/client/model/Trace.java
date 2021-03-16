package com.swisscom.ais.itext.client.model;

/**
 * Bundles tracking information for an ongoing transaction/request. It is meant to be passed from one layer to another and
 * its information be used whenever logging/auditing/tracing activities are performed.
 */
public class Trace {

    /**
     * The ID of this trace. In most cases this is an UUID.
     */
    private String id;

    public Trace(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
