package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.SalaryType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Salary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    private AppUser user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    @Enumerated(EnumType.STRING)
    private SalaryType salaryType;

    @Column(precision = 10, scale = 2)
    BigDecimal price;

    private Boolean isActive;

    protected LocalDate start;

    protected LocalDate finish;
}
