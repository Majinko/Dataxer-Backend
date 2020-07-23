package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.DocumentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DocumentPackDTO {
    private Long documentPackId;
    private Long documentId;
    private Enum<DocumentType> type;
    List<DocumentPackItemDTO> packItems = new ArrayList<>();
    Integer position;
    String title;
    Integer tax;
    private BigDecimal totalPrice;
}
