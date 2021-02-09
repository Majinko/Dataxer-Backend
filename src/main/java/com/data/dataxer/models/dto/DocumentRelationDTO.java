package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.DocumentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DocumentRelationDTO {
    private Long relatedDocumentId;
    private String documentTitle;
    private String number;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private LocalDate createdDate;

    private DocumentType documentType;
}
