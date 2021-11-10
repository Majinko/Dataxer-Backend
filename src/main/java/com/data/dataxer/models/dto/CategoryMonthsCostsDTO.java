package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;

@Getter
@Setter
public class CategoryMonthsCostsDTO implements Comparable<CategoryMonthsCostsDTO> {
    private Long categoryId;
    private Long categoryParentId;
    private Integer categoryDepth;
    private String categoryName;
    private HashMap<Integer, BigDecimal> totalMonthsCosts;
    private BigDecimal categoryTotalPrice;
    private Boolean hasChildren;

    @Override
    public int compareTo(@NotNull CategoryMonthsCostsDTO o) {
        return o.categoryTotalPrice.compareTo(this.categoryTotalPrice);
    }
}
