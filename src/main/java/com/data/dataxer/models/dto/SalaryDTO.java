package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.enums.SalaryType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class SalaryDTO {
    private Long id;
    private AppUser user;
    private SalaryType salaryType;
    private BigDecimal price;
    private Boolean isActive;
    protected LocalDate start;
    protected LocalDate finish;
}
