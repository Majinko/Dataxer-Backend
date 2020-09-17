package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactDTO {
    private Long id;
    private String name;
    private String photoUrl;
    private String street;
    private String city;
    private String country;
    private String postalCode;
    private String regNumber;
    private String email;
    private String phone;
    private String cin;
    private String tin;
    private String vatin;
}
