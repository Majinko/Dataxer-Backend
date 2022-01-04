package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.SalaryType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;

@Getter
@Setter
public class UserHourOverviewDTO {

    private String firstName;

    private String lastName;

    private String fullName;

    private String photoUrl;

    private SalaryType salaryType;

    private BigDecimal activeHourPrice;

    private HashMap<Integer, String> userHours;

    private HashMap<Integer, BigDecimal> userTimePrices;

    private String totalUserHours;

    private BigDecimal totalUserPrice;

}
