package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Company;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataxerUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
