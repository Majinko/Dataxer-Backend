package com.data.dataxer.models.enums;

public enum CostState {
    PAYED("payed"),
    UNPAID("unpaid"),
    OVERDUE("overdue");

    private String state;

    CostState(String state) {
        this.state = state;
    }

    public static CostState getByName(String state) {
        return CostState.valueOf(state);
    }

    public String getState() {
        return this.state;
    }

}
