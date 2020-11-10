package com.data.dataxer.models.domain;

import com.data.dataxer.mappers.HashMapConverter;
import com.data.dataxer.models.enums.CostState;
import com.data.dataxer.models.enums.CostType;
import com.data.dataxer.models.enums.CostsPeriods;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Getter
@Setter
public class Cost extends BaseEntitySoftDelete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    Project project;

    private String title;

    private String costOrder;

    private String category;

    protected String number;

    private String variableSymbol;

    private String constantSymbol;

    private String currency;

    @Column(columnDefinition = "text")
    private String note;

    @Enumerated(EnumType.STRING)
    private CostState state;

    @Enumerated(EnumType.STRING)
    private CostType type;

    @Enumerated(EnumType.STRING)
    private CostsPeriods period;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Boolean isInternal;

    private Boolean isRepeated;

    @Column(columnDefinition = "text")
    @Convert(converter = HashMapConverter.class)
    protected Map<String, Object> costData;

    private BigDecimal price;

    private Integer tax;

    private BigDecimal totalPrice;

    private LocalDate repeatedFrom;

    private LocalDate repeatedTo;

    private LocalDate nextRepeatedCost;

    private LocalDate createdDate; //datum vystavenia

    private LocalDate dueDate;

    protected LocalDate deliveredDate;

    protected LocalDate taxableSupply; // datum uplatnenia DPH

    public Cost() {
    }

    public Cost(Cost existedCost) {
        this.title = existedCost.getTitle();
        this.contact = existedCost.getContact();
        this.project = existedCost.getProject();
        this.costOrder = existedCost.getCostOrder();
        this.state = existedCost.getState();
        this.currency = existedCost.getCurrency();
        this.category = existedCost.getCategory();
        this.type = existedCost.getType();
        this.note = existedCost.getNote();
        this.isInternal = existedCost.getIsInternal();
        this.isRepeated = existedCost.getIsRepeated();
        this.period = existedCost.getPeriod();
        this.costData = existedCost.getCostData();
        this.price = existedCost.getPrice();
        this.tax = existedCost.getTax();
        this.totalPrice = existedCost.getTotalPrice();
        this.repeatedFrom = existedCost.getRepeatedFrom();
        this.repeatedTo = existedCost.getRepeatedTo();
        this.nextRepeatedCost = existedCost.getNextRepeatedCost();
        this.createdDate = LocalDate.now();
        this.dueDate = existedCost.getDueDate();
        this.deliveredDate = existedCost.getDeliveredDate();
        this.taxableSupply = existedCost.getTaxableSupply();
    }
}
