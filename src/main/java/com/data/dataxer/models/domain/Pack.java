package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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
public class Pack extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "pack", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<PackItem> packItems = new HashSet<>();
}
