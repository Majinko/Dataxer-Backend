package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class CategoryCostsOverviewDTO {
    private List<CategoryMonthsCostsDTO> categoryMonthsCostsDTOS;
    private HashMap<Integer, BigDecimal> monthsTotalCosts;
    private BigDecimal totalCosts;
}
