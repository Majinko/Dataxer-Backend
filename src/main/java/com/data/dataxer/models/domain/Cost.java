package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.CostState;
import com.data.dataxer.models.enums.CostType;
import com.data.dataxer.models.enums.CostsPeriods;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE cost SET deleted_at = now() WHERE id = ?")
public class Cost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact contact;

    private String title;

    private String costOrder;

    private CostState state;

    private String category;

    private CostType type;

    private Boolean isInternal;

    private Boolean isRepeated;

    private CostsPeriods period;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private LocalDate repeatedFrom;

    private LocalDate repeatedTo;

    private LocalDate nextRepeatedCost;

    //datum vystavenia
    private LocalDate dateOfCreate;

    private LocalDate dueDate;

    private LocalDate deletedAt;

    public Cost() {}

    public Cost(Cost existedCost) {
        this.title = existedCost.getTitle();
        this.costOrder = existedCost.getCostOrder();
        this.state = existedCost.getState();
        this.category = existedCost.getCategory();
        this.type = existedCost.getType();
        this.isInternal = existedCost.getIsInternal();
        this.isRepeated = existedCost.getIsRepeated();
        this.period = existedCost.getPeriod();
        this.price = existedCost.getTotalPrice();
        this.repeatedFrom = existedCost.getRepeatedFrom();
        this.repeatedTo = existedCost.getRepeatedTo();
        this.nextRepeatedCost = existedCost.getNextRepeatedCost();
        this.dateOfCreate = LocalDate.now();
        this.dueDate = existedCost.getDueDate();
    }

}
