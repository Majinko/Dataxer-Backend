package com.data.dataxer.models.domain;

import com.data.dataxer.securityContextUtils.SecurityUtils;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

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
public class Time extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    Project project;

    @OneToOne(fetch = FetchType.LAZY)
    AppUser user;

    @OneToOne(fetch = FetchType.LAZY)
    Category category;

    Integer time;

    Integer timeFrom;

    Integer timeTo;

    @Column(precision = 10, scale = 2)
    BigDecimal price;

    @Column(columnDefinition = "text")
    String description;

    float km;

    private LocalDate dateWork;

    @PrePersist
    private void persist() {
        user = SecurityUtils.loggedUser();
    }
}
