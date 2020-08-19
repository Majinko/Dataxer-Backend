package com.data.dataxer.models.enums;

public enum DocumentType {
    PRICE_OFFER("priceOffer"),
    INVOICE("invoice");

    private String name;

    DocumentType(String name) {
        this.name = name;
    }

    public static DocumentType getTypeByName(String name) {
        return  DocumentType.valueOf(name);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
