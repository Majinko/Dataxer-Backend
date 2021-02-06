package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class BankAccountDTO {
    private Long id;
    Integer bankCode;
    BigInteger accountNumber;
    String bankName;
    String currency;
    String iban;
    String swift;
    Boolean isDefault;
}
