package com.data.dataxer.models.enums;

public enum PaymentMethod {

    CASH("cash"),
    DEBIT_CARD("debit_card"),
    CREDIT_CARD("credit_card"),
    BANK_PAYMENT("bank_payment"),
    PAYPAL("paypal");

    private String name;

    PaymentMethod(String name) {
        this.name = name;
    }

    public PaymentMethod getPaymentMethodByName(String name) {
        return PaymentMethod.valueOf(name);
    }

    public String getName() {
        return this.name;
    }

}
