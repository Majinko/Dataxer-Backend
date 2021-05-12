package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String name;
    private Integer lft;
    private Integer rgt;
    private Long position;
    private Integer depth;
    private CategoryDTO parent;
}
