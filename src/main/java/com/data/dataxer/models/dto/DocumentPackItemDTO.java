package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DocumentPackItemDTO {
    private Long id;
    private ItemDTO item;
    private Float qty;
    private String title;
    Integer position;
    private BigDecimal discount;
    private BigDecimal price;
    private Integer tax;
    private BigDecimal totalPrice;
}
