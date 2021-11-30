package com.data.dataxer.models.enums;

public enum CostType {
    INVOICE("invoice"),
    BILL("bill"),
    INTERNAL("internal"),
    CONTRIBUTION("contribution"),
    RECEIVED_CREDIT_NOTE("received_credit_note"),
    OTHER("other");

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
