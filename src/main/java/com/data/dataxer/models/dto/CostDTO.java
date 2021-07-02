package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.enums.CostType;
import com.data.dataxer.models.enums.CostsPeriods;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CostDTO {
    private Long id;

    private DocumentState state;
    private CostType type;
    private PaymentMethod paymentMethod;
    private CostsPeriods period;
    private String note;
    private String title;
    private String number;
    private String currency;
    private String variableSymbol;
    private String constantSymbol;
    private Boolean isInternal;
    private Boolean isRepeated;
    private LocalDate paymentDate;
    private LocalDate repeatedFrom;
    private LocalDate repeatedTo;
    private LocalDate nextRepeatedCost;
    private LocalDate createdDate;
    private LocalDate dueDate;
    private LocalDate deliveredDate;
    private LocalDate taxableSupply;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private Integer tax;

    Contact contact;
    Project project;

    private List<StorageFileDTO> files = new ArrayList<>();
    private List<CategoryDTO> categories = new ArrayList<>();
}
