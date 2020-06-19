package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class PriceOfferPackDTO {
    private Long id;
    Set<PriceOfferPackItemDTO> items = new HashSet<>();
    String title;
    Integer tax;
    private BigDecimal totalPrice;
}
