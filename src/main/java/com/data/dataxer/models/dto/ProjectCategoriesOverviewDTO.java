package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectCategoriesOverviewDTO {

    private List<ProjectTimePriceOverviewDTO> userOverviewDTOList;

    private Category category;

}
