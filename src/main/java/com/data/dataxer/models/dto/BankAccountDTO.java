package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankAccountDTO {
    private Long id;
    Integer bankCode;
    Integer accountNumber;
    String bankName;
    String currency;
    String iban;
    String swift;
    Boolean isDefault;
}
