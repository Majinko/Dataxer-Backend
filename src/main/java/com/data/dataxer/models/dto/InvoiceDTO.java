package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class InvoiceDTO {
    private Long invoiceId;
    private String title;
    private String number;
    private String state;
    private String note;
    private BigDecimal priceWithoutTax;
    private Map<String, Object> invoiceData;
    private LocalDate createdDate;
    private LocalDate deliveryDate;
    private LocalDate paymentDate;
    private LocalDate dueDate;
    private LocalDateTime deletedAt;

    private ContactDTO contact;
    List<DocumentPackDTO> packs = new ArrayList<>();
}
