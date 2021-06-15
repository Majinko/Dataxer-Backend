package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EvaluationDTO {

    private BigDecimal profit;
    private BigDecimal profitManHour;
    private BigDecimal rebate;

    private BigDecimal project;
    private BigDecimal projectStatistic;
    private BigDecimal realization;

    private ProjectStatisticDTO projectStatisticDTO;

}
