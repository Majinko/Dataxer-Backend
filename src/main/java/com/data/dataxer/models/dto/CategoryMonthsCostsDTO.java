package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;

@Getter
@Setter
public class CategoryMonthsCostsDTO {

    private String categoryName;
    private HashMap<Integer, BigDecimal> totalMonthsCosts;
    private BigDecimal categoryTotalPrice;

}
