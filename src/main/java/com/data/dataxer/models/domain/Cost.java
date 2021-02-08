package com.data.dataxer.models.domain;

import com.data.dataxer.mappers.HashMapConverter;
import com.data.dataxer.models.enums.CostState;
import com.data.dataxer.models.enums.CostType;
import com.data.dataxer.models.enums.CostsPeriods;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE cost SET deleted_at = now() WHERE id = ?")
@FilterDef(
        name = "companyCondition",
        parameters = @ParamDef(name = "companyId", type = "long")
)
@Filter(
        name = "companyCondition",
        condition = "company_id = :companyId"
)
public class Cost extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    Project project;

    @JoinTable(
            name = "storage",
            joinColumns = @JoinColumn(name = "fileAbleId"),
            inverseJoinColumns = @JoinColumn(name = "id"),
            foreignKey = @javax.persistence.ForeignKey(name = "none")
    )
    @OneToMany(fetch = FetchType.LAZY)
    @Where(clause="file_able_type='cost'")
    private List<Storage> files = new ArrayList<>();

    private String title;

    private String costOrder;

    @OneToOne
    private Category category;

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

    private LocalDate paymentDate;

    private LocalDate repeatedFrom;

    private LocalDate repeatedTo;

    private LocalDate nextRepeatedCost;

    private LocalDate createdDate; //datum vystavenia

    private LocalDate dueDate;

    protected LocalDate deliveredDate;

    protected LocalDate taxableSupply; // datum uplatnenia DPH

    private LocalDateTime deletedAt;
}
