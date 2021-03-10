package com.swisscom.ais.itext.client.model;

public enum VerboseLevel {
    LOW(0), BASIC(1), MEDIUM(2), HIGH(3);

    private final int importance;

    private VerboseLevel(int importance) {
        this.importance = importance;
    }

    public int getImportance() {
        return importance;
    }
}
