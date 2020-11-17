package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE payment SET deleted_at = now() WHERE id = ?")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;

    //is added for better filtering option and to find way how change state of document type to PAYED state
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private BigDecimal payedValue;

    @Column(nullable = false)
    private Boolean taxDocumentCreated = false;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private LocalDate payedDate;

    private LocalDate deletedAt;
}
