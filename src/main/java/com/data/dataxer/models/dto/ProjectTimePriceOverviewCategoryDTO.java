package com.data.dataxer.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectTimePriceOverviewCategoryDTO {
    private Long categoryId;
    private String categoryName;
    private Integer categoryTimeSum;
    private List<UserTimePriceOverviewDTO> userTimePriceOverviewList;
}
