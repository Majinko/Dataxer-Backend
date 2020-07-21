package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DocumentPackDTO {
    private Long documentPackId;
    private DocumentDTO document;
    List<DocumentPackItemDTO> packItems = new ArrayList<>();
    Integer position;
    String title;
    Integer tax;
    private BigDecimal totalPrice;
}
