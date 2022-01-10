package com.data.dataxer.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BankAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
    private Company company;

    Integer bankCode;

    BigInteger accountNumber;

    String bankName;

    String currency;

    @Column(length = 50)
    String iban;

    @Column(length = 50)
    String swift;

    @Column(columnDefinition = "boolean default false")
    Boolean isDefault;
}
