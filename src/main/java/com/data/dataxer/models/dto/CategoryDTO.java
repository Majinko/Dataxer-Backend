package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryDTO {
    private Long id;
    private String name;
    private Integer position;
    //potrebujeme aby sme vedeli vyhladavat pomocou integrovanych metod
    private Long treeId;
    private Long lft;
    private Long rgt;
}
