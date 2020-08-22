package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.DocumentState;
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
    private Long id;
    private String title;
    private String number;
    private DocumentState.InvoiceStates state;
    private String note;
    private BigDecimal price;
    private BigDecimal priceTotal;
    private Map<String, Object> invoiceData;
    private LocalDate createdDate;
    private LocalDate deliveredDate;
    private LocalDate paymentDate;
    private LocalDate dueDate;
    private LocalDateTime deletedAt;

    private ContactDTO contact;
    List<DocumentPackDTO> packs = new ArrayList<>();
}
