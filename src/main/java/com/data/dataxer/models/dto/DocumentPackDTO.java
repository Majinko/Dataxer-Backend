package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DocumentPackDTO {
    private Long id;
    List<DocumentPackItemDTO> packItems = new ArrayList<>();
    Integer position;
    String title;
    Integer tax;
    private Boolean customPrice;
    private Boolean showItems;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
