package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.CategoryGroup;
import com.data.dataxer.models.enums.CategoryType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CategoryNestedDTO {
    private Long id;
    private String name;
    private Long depth;
    private Long position;
    private Long parentId;

    private CategoryType categoryType;
    private CategoryGroup categoryGroup;

    private List<CategoryNestedDTO> children = new ArrayList<>();
}
