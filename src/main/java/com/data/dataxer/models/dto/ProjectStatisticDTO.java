package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ProjectStatisticDTO {

    private LocalDate projectStart;
    private LocalDate projectEnd;
    private Integer projectTakeInMonths;
    private String projectManHours;
    private String averageManHoursForMonth;
    private BigDecimal averageHourNetto;
    private BigDecimal averageHourBrutto;
    private BigDecimal projectPayedPrice;
    private BigDecimal projectPayedCosts;
    private BigDecimal projectPayedInternalCosts;
    private BigDecimal projectRunningCosts;

    //just for other math purpose
    private BigDecimal projectHourBruttoSum;
}
