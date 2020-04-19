package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE contact SET deleted_at = now() WHERE id = ?")
public class Company implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private DataxerUser dataxerUser;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillingInformation> billingInformation = new ArrayList<>();

    private String logoUrl;

    private String legalForm;

    private String street;

    private String city;

    private String postalCode;

    private String country;

    private String email;

    private String phone;

    private String web;

    private String identifyingNumber;

    private String vat;

    private String netOfVat;

    private String iban;

    private LocalDateTime deletedAt;
}
