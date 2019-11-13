package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Time {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    Project project;

    @OneToOne
    User user;

    @OneToOne
    Category category;

    int time;

    int timeFrom;

    int timeTo;

    @Column(precision = 10, scale = 2)
    BigDecimal price;

    @Column(columnDefinition = "text")
    String description;

    private LocalDateTime dateWork;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
