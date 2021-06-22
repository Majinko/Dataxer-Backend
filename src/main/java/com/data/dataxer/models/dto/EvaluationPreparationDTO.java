package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EvaluationPreparationDTO {

    private BigDecimal hourTime;
    private BigDecimal profit;
    private BigDecimal profitManHour;
    private BigDecimal rebate;

    //for finalization on second request
    private BigDecimal projectInvoicesPriceSum;
    private BigDecimal projectCostPriceSum;

}
