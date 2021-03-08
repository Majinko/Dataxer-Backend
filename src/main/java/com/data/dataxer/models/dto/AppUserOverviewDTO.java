package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AppUserOverviewDTO {
    Long id;
    String fullName;
    LocalDate startWork;
    Long years;
    Long projectCount;
    Integer sumTime;
    SalaryDTO salaryDTO;
}
