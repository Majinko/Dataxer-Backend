package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDTO {
    private Long id;
    private String legalForm;
    private String name;
    private String street;
    private String city;
    private String postalCode;
    private String country;
    private String email;
    private String phone;
    private String web;
    private String identifyingNumber;
    private String vat;
    private String netOfVat;
    private String iban;
    private String cin;
    private String tin;
    private String vatin;
    private Boolean defaultCompany;

}
