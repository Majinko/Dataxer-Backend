package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class PriceOfferPackDTO {
    private Long id;
    List<PriceOfferPackItemDTO> items = new ArrayList<>();
    String title;
    Integer tax;
    private BigDecimal totalPrice;
}
