package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Company;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AppUserDTO {
    private Long id;
    private String uid;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String street;
    private String city;
    private String postalCode;
    private String country;
    private List<Company> companies;
}
