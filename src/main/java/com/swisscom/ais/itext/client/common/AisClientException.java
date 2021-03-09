package com.swisscom.ais.itext.client.common;

public class AisClientException extends RuntimeException {

    public AisClientException(String message) {
        super(message);
    }

    public AisClientException(String message, Throwable cause) {
        super(message, cause);
    }

}
