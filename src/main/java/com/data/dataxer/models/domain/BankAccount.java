package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class BankAccount extends BaseEntitySoftDelete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    Integer bankCode;

    Integer accountNumber;

    String bankName;

    String currency;

    @Column(length = 50)
    String iban;

    @Column(length = 50)
    String swift;

    @Column(columnDefinition = "boolean default false")
    Boolean isDefault;
}
