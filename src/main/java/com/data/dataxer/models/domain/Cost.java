package com.data.dataxer.models.domain;

import com.data.dataxer.mappers.HashMapConverter;
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
import java.util.Map;

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

    @OneToOne
    private Category category;

    private CostType type;

    private Boolean isInternal;

    private Boolean isRepeated;

    private CostsPeriods period;

    @Column(columnDefinition = "text")
    @Convert(converter = HashMapConverter.class)
    protected Map<String, Object> documentData;

    private BigDecimal price;

    private BigDecimal totalPrice;

    private LocalDate repeatedFrom;

    private LocalDate repeatedTo;

    private LocalDate nextRepeatedCost;

    //datum vystavenia
    private LocalDate dateOfCreate;

    private LocalDate dueDate;

    private LocalDate deletedAt;

}
