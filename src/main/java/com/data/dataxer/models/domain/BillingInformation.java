package com.data.dataxer.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@FilterDef(
        name = "companyCondition",
        parameters = @ParamDef(name = "companyId", type = "long")
)
@Filter(
        name = "companyCondition",
        condition = "company_id = :companyId"
)
public class BillingInformation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id", insertable = true, nullable = false)
    private Company company;

    private String street;

    private String city;

    private String postalCode;

    private String country;
}
