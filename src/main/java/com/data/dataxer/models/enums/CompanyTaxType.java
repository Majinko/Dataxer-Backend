package com.data.dataxer.models.enums;

public enum CompanyTaxType {
    TAX_PAYER("taxPayer"),
    NO_TAX_PAYER("noTaxPayer");

    private  String taxType;

    CompanyTaxType(String taxType) {
        this.taxType = taxType;
    }
}
