package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.DeliveryMethod;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.PaymentMethod;
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
    private String variableSymbol;
    private String specificSymbol;
    private String subject;
    private DeliveryMethod deliveryMethod;
    private PaymentMethod paymentMethod;
    private String headerComment;
    private DocumentState state;
    private String note;
    private BigDecimal discount;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private Map<String, Object> documentData;
    private LocalDate createdDate;
    private LocalDate deliveredDate;
    private LocalDate paymentDate;
    private LocalDate dueDate;
    private LocalDateTime deletedAt;
    private DocumentType documentType;

    private ContactDTO contact;
    private ProjectDTO project;
    private CompanyDTO company;
    List<PaymentDTO> payments = new ArrayList<>();
    List<DocumentPackDTO> packs = new ArrayList<>();
}
