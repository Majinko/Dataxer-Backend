package com.data.dataxer.models.domain;

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
