package com.bwts.batchservice.entity;

public enum DocumentStatus {
    START(0), FAILED(-1), SUCCESS(1),RETRY_FAILED(-2),PERMANENTLY_FAILED(-3),MIGRATION_RETRY_FAILED(-4);

    private final int value;

    public int getValue() {
        return value;
    }

    DocumentStatus(int value) {
        this.value = value;
    }

    public static DocumentStatus fromValue(int value) {
        for (DocumentStatus status : DocumentStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
