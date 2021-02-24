package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryStoreDTO {

    private String name;

    private CategoryDTO parent;

}
