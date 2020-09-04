package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PaymentDTO {

    private Long id;
    private Long documentId;
    private DocumentType documentType;
    private BigDecimal payedValue;
    private PaymentMethod paymentMethod;
    private LocalDate payedDate;

}
