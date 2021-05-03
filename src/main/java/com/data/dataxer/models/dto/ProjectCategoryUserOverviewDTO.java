package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProjectCategoryUserOverviewDTO {
    private String categoryName;
    private String hoursInCategory;

    private BigDecimal hourNetto;
    private BigDecimal priceNetto;

    private BigDecimal hourBrutto;
    private BigDecimal priceBrutto;
}
