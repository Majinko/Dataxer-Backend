package com.data.dataxer.models.enums;

public enum InvoiceType {

    PROFORMA("proforma"),
    TAX_DOCUMENT("tax_document"),
    SUMMARY_INVOICE("summary_invoice");

    private String name;

    InvoiceType(String name) {
        this.name = name;
    }

    public InvoiceType getByName(String name) {
        return InvoiceType.valueOf(name);
    }

    public String getName() {
        return this.name;
    }


}
