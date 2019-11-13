package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.enums.ItemPriceLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ItemDTO {
    private Long id;
    String code;
    String title;
    String model;
    String manufacturer;
    ItemPriceLevel itemPriceLevel;
    String timeDelivery;
    String text;
    private BigDecimal price;
    private BigDecimal wholesalePrice;
    private Integer tax;
    private Integer wholesaleTax;
    private List<Category> categories;
}
