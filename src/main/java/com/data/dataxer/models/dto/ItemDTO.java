package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.ItemPrice;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDTO {
    private Long id;

    Category category;
    String title;
    String type;
    String shortDescription;
    String description;
    String manufacturer;
    Integer supplierId;
    String web;
    String unit;
    String code;
    String dimensions;
    Boolean isPartOfSet;
    Boolean needMontage;
    String priceLevel;
    String model;
    String series;
    String color;
    String material;
    ItemPrice itemPrice;
}
