package com.db.bex.dbTrainingEnroll.entity;

public enum UrgencyType {
    LOW ("LOW"),
    MEDIUM ("MEDIUM"),
    HIGH ("HIGH");

    private final String urgency;

    UrgencyType(String urgency) {
        this.urgency = urgency;
    }

    @Override
    public String toString() {
        return this.urgency;
    }
}
