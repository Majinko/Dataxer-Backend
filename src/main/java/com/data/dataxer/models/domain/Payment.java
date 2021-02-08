package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE payment SET deleted_at = now() WHERE id = ?")
@FilterDef(
        name = "companyCondition",
        parameters = @ParamDef(name = "companyId", type = "long")
)
@Filter(
        name = "companyCondition",
        condition = "company_id = :companyId"
)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long documentId;

    //is added for better filtering option and to find way how change state of document type to PAYED state
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private BigDecimal payedValue;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private LocalDate payedDate;

    private LocalDate deletedAt;
}
