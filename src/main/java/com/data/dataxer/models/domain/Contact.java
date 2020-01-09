package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Contact extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String companyName;

    private String street;

    private String city;

    private String country;

    private String postalCode;

    private String regNumber;

    private String email;

    private String phone;

    @Column(columnDefinition = "text")
    private String note;

    private LocalDateTime deletedAt;
}
