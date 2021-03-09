package com.swisscom.ais.itext.client.model;

public enum VerbosityLevel {
    LOW(0), BASIC(1), MEDIUM(2), HIGH(3);

    private final int importance;

    private VerbosityLevel(int importance) {
        this.importance = importance;
    }

    public int getImportance() {
        return importance;
    }
}
