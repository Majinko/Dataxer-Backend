package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.CostType;
import com.data.dataxer.models.enums.DocumentState;
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
@SQLDelete(sql = "UPDATE invoice SET deleted_at = now() WHERE id = ?")
public class Cost extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact contact;

    private String title;

    private DocumentState state;

    private String category;

    private CostType type;

    private Boolean isInternal;

    private BigDecimal price;

    private BigDecimal totalPrice;

    //datum vystavenia
    private LocalDate dateOfCreate;

    private LocalDate dueDate;

    private LocalDate deletedAt;

}
