package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.CategoryGroup;
import com.data.dataxer.models.enums.CategoryType;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private Long parentId;
    private String name;
    private Integer position;
    private CategoryGroup categoryGroup;
    private CategoryType categoryType;
}
