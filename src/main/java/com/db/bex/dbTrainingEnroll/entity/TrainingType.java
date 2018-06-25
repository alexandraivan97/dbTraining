package com.db.bex.dbTrainingEnroll.entity;

public enum TrainingType {
    BUILD ("BUILD"),
    GROW ("GROW");

    private final String trainingType;

    TrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    @Override
    public String toString() {
        return this.trainingType;
    }
}
