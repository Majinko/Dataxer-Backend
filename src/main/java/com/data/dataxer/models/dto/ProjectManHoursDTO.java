package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProjectManHoursDTO {
    private BigDecimal sumPriceNetto;
    private BigDecimal sumPriceBrutto;

    List<UserTimePriceOverviewDTO> userTimePriceOverviewList;

    public ProjectManHoursDTO() {
        this.sumPriceNetto = BigDecimal.ZERO;
        this.sumPriceBrutto = BigDecimal.ZERO;
    }
}
