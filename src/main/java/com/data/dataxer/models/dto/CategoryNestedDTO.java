package com.data.dataxer.models.dto;

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

    private List<CategoryNestedDTO> children = new ArrayList<>();
}
