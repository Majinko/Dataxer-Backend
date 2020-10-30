package com.data.dataxer.services;

import com.data.dataxer.models.domain.BankAccount;
import java.util.List;

public interface BankAccountService {
    BankAccount store(BankAccount bankAccount);

    BankAccount update(BankAccount bankAccount);

    List<BankAccount> findAll();

    BankAccount getById(Long id);

    BankAccount getDefaultBankAccount();

    void setDefaultBankAccount(Long id);

    void destroy(Long id);
}
