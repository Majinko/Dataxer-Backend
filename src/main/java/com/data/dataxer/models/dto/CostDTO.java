package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.enums.CostState;
import com.data.dataxer.models.enums.CostType;
import com.data.dataxer.models.enums.CostsPeriods;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CostDTO {
    private Long id;
    private String title;
    private CostState state;
    private CostType type;
    private String category;
    private String note;
    private String number;
    private String currency;
    private String variableSymbol;
    private String constantSymbol;
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
    private Integer tax;

    Contact contact;
    Project project;
}
