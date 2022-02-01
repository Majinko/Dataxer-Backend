package com.data.dataxer.models.enums;

public enum AuditLogAction {

    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete");

    public final String name;

    AuditLogAction(String name) {
        this.name = name;
    }

}
