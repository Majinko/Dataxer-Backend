package com.data.dataxer.models.enums;

public enum MailTemplateType {
    INVOICE("invoice"),
    PROFORMA("proforma"),
    TASK("task");

    public final String type;

    MailTemplateType(String type) {
        this.type = type;
    }
}
