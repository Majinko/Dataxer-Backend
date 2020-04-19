package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ItemPriceDTO
{
    private Long id;
    private BigDecimal wholesalePrice;
    private Integer tax;
    private Integer wholesaleTax;
    private Float marge;
    private Float surcharge;
}
