package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.enums.CostType;
import com.data.dataxer.models.enums.CostsPeriods;
import com.data.dataxer.models.enums.DocumentState;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CostDTO {

    private Long id;
    private String title;
    private DocumentState state;
    private CostType type;
    private String category;
    private Boolean isInternal;
    private Boolean isRepeated;
    private CostsPeriods period;
    private LocalDate repeatedFrom;
    private LocalDate repeatedTo;
    private LocalDate nextRepeatedCost;
    private LocalDate dateOfCreate;
    private LocalDate dueDate;
    private BigDecimal price;
    private BigDecimal totalPrice;

    Contact contact;
}
