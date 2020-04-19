package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Company;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataxerUserDTO {
    private Long id;
    private String uid;
    private String email;
    private List<Company> companies;
}
