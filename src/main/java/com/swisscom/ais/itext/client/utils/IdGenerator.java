package com.swisscom.ais.itext.client.utils;

import java.util.UUID;

public class IdGenerator {

    private static final String ID_PREFIX = "ID-";
    private static final String DOC_PREFIX = "DOC-";

    public static String generateRequestId() {
        return ID_PREFIX + generateId();
    }

    public static String generateDocumentId() {
        return DOC_PREFIX + generateId();
    }

    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
