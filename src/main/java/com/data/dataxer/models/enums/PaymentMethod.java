package com.data.dataxer.models.enums;

public enum PaymentMethod {

    CASH("CASH"),
    DEBIT_CARD("DEBIT CARD"),
    CREDIT_CARD("CREDIT CARD"),
    BANK_PAYMENT("BANK PAYMENT"),
    PAYPAL("PAYPAL");

    private String name;

    PaymentMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static PaymentMethod getByName(String name) {
        return PaymentMethod.valueOf(name);
    }

}
