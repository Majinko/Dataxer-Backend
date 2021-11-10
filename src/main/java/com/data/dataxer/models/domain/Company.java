package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.CompanyTaxType;
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
@SQLDelete(sql = "UPDATE company SET deleted_at = now() WHERE id = ?")
public class Company implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50)
    private String name;

    @JoinTable(
            name = "app_user_companies",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "app_user_id")
    )
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<AppUser> appUsers = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private CompanyTaxType companyTaxType;

    @Column(columnDefinition = "text")
    private String logoUrl;

    @Column(columnDefinition = "text")
    private String signatureUrl;

    private String legalForm;

    private String street;

    private String city;

    private String postalCode;

    private String country;

    private String email;

    private String phone;

    private String web;

    private String iban;

    @Column(length = 10)
    private String cin;

    private String tin;

    private String vatin;

    private LocalDateTime deletedAt;
}
