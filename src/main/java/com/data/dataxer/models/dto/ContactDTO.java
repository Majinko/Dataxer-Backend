package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Project;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Transactional
public class ContactDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private String street;
    private String city;
    private String country;
    private String postalCode;
    private String regNumber;
    private String email;
    private String phone;
}
