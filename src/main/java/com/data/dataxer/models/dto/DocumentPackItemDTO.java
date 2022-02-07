package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DocumentPackItemDTO {
    private Long id;
    private Float qty;
    private String title;
    private String unit;
    private Integer position;
    private BigDecimal discount;
    private BigDecimal price;
    private Integer tax;
    private BigDecimal totalPrice;

    private ItemDTO item;
    private CategoryDTO category;
}
