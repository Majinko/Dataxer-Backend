package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDTO {
    private Long id;
    private String name;
    private String legalForm;
    private String street;
    private String city;
    private String postalCode;
}
