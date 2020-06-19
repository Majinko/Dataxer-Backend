package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class PriceOfferPackItemDTO {
    private Long id;
    private ItemDTO item;
    private String title;
    private Float qty;
    private BigDecimal price;
    private Integer tax;
    private BigDecimal totalPrice;
}
