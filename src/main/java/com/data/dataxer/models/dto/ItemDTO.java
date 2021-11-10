package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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
    String previewUrl;

    ContactDTO supplier;
    ItemPriceDTO itemPrice;

    private List<StorageFileDTO> files = new ArrayList<>();
    private List<CategoryDTO> categories = new ArrayList<>();
}
