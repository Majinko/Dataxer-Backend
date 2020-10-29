package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE contact SET deleted_at = now() WHERE id = ?")
public class BankAccount extends BaseEntity {
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

    @Column(columnDefinition = "default 0")
    Boolean isDefault;

    private LocalDateTime deletedAt;
}
