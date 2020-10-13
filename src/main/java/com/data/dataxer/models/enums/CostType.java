package com.data.dataxer.models.enums;

public enum CostType {

    INVESTMENT("investment"),
    OUTGOING("outgoing");

    private String name;

    CostType(String name) {
        this.name = name;
    }

    public static CostType getByName(String name) {
        return CostType.valueOf(name);
    }

    public String getName() {
        return this.name;
    }

}
