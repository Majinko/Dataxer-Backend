package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.BillingInformation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyDTO {
    private Long id;
    private List<BillingInformation> billingInformation;
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
}
