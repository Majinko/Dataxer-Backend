package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.DeliveryMethod;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
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

    private ContactDTO contact;
    List<DocumentPackDTO> packs = new ArrayList<>();
}
