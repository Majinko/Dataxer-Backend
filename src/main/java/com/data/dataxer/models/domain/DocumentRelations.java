package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@FilterDef(
        name = "companyCondition",
        parameters = @ParamDef(name = "companyId", type = "long")
)
@Filter(
        name = "companyCondition",
        condition = "company_id = :companyId"
)
public class DocumentRelations extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long documentId;

    private Long relationDocumentId;
}
