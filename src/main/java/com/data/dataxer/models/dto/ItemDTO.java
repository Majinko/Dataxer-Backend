package com.data.dataxer.models.dto;
import com.data.dataxer.models.domain.Storage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemDTO {
    private Long id;

    String title;
    String type;
    String shortDescription;
    String description;
    String manufacturer;
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

    ContactDTO supplier;
    CategoryDTO category;
    ItemPriceDTO itemPrice;

    private List<Storage> storage;
}
