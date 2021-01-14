package com.data.dataxer.models.domain;

import com.data.dataxer.securityContextUtils.SecurityUtils;
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

    // todo set fetch type lazy
    @OneToOne
    Project project;

    // todo set fetch type lazy
    @OneToOne
    AppUser user;

    // todo set fetch type lazy
    @OneToOne
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
