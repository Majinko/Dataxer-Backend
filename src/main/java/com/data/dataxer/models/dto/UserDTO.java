package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Company;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Company company;
}
