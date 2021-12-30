package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class PriceOfferDTO {
    private Long id;
    private String title;
    private String subject;
    private String number;
    private DocumentState state;
    private String note;
    private Map<String, Object> documentData;
    private LocalDate createdDate;
    private LocalDate deliveredDate;
    private LocalDate dueDate;
    private BigDecimal discount;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private DocumentType documentType;

    private ContactDTO contact;
    private ProjectDTO project;
    List<DocumentPackDTO> packs = new ArrayList<>();
}
