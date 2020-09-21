package com.data.dataxer.models.enums;

public enum ProjectState {
    COMPLETED("completed"),
    IN_PROGRESS("in_progress");

    private String stateCode;

    ProjectState(String stateCode) {
        this.stateCode = stateCode;
    }

    public static ProjectState getStateByCode(String stateCode) {
        return  ProjectState.valueOf(stateCode);
    }

    public String getStateCode() {
        return this.stateCode;
    }

    @Override
    public String toString() {
        return this.stateCode;
    }
}
