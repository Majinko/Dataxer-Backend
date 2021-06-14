package com.data.dataxer.models.enums;

public enum MailTemplateType {
    INVOICE("invoice"),
    PROFORMA("proforma");

    public final String type;

    MailTemplateType(String type) {
        this.type = type;
    }
}
