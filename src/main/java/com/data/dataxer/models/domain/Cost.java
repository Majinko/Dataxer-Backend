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
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE contact SET deleted_at = now() WHERE id = ?")
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

    private LocalDate repeatedFrom;

    private LocalDate repeatedTo;

    private LocalDate nextRepeatedCost;

    private LocalDate createdDate; //datum vystavenia

    private LocalDate dueDate;

    protected LocalDate deliveredDate;

    protected LocalDate taxableSupply; // datum uplatnenia DPH

    private LocalDateTime deleteAt;
}
