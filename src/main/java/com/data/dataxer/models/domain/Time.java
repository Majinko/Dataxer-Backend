package com.data.dataxer.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Time extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    Project project;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    AppUser user;

    @OneToOne(fetch = FetchType.LAZY)
    Category category;

    @OneToOne(fetch = FetchType.LAZY)
    Salary salary;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    Integer time;

    Integer timeFrom;

    Integer timeTo;

    @Column(precision = 10, scale = 2)
    BigDecimal price;

    @Column(columnDefinition = "text")
    String description;

    float km;

    private LocalDate dateWork;
}
