package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String street;
    private String town;
    private String country;
    private String postalCode;
    private String regNumber;
    private String email;
    private String phone;
}
