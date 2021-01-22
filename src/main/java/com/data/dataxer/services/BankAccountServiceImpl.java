package com.data.dataxer.services;

import com.data.dataxer.models.domain.BankAccount;
import com.data.dataxer.repositories.BankAccountRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public BankAccount store(BankAccount bankAccount) {
        BankAccount bank = bankAccountRepository.save(bankAccount);

        this.setDefaultBankAccount(bank.getId());

        return bank;
    }

    @Override
    public BankAccount update(BankAccount bankAccount) {
        return this.bankAccountRepository.findById(bankAccount.getId()).map(account -> {

            account.setAccountNumber(bankAccount.getAccountNumber());
            account.setBankCode(bankAccount.getBankCode());
            account.setBankName(bankAccount.getBankName());
            account.setCurrency(bankAccount.getCurrency());
            account.setIban(bankAccount.getIban());
            account.setSwift(bankAccount.getSwift());

            return bankAccountRepository.save(account);
        }).orElse(null);
    }

    @Override
    public List<BankAccount> findAll() {
        return this.bankAccountRepository.findAllByCompanyIdIn(SecurityUtils.companyIds());
    }

    @Override
    public BankAccount getById(Long id) {
        return bankAccountRepository.findByIdAndCompanyIdIn(id, SecurityUtils.companyIds()).orElseThrow(
                () -> new RuntimeException("Bank account not found")
        );
    }

    @Override
    public BankAccount getDefaultBankAccount() {
        return this.bankAccountRepository.findByIsDefaultAndCompanyIdIn(true, SecurityUtils.companyIds()).orElseThrow(
                () -> new RuntimeException("Default account not found, please set it")
        );
    }

    @Override
    public void setDefaultBankAccount(Long id) {
        List<BankAccount> bankAccounts = bankAccountRepository.findAllByCompanyIdIn(SecurityUtils.companyIds());

        bankAccounts.forEach(bankAccount -> {
            bankAccount.setIsDefault(bankAccount.getId().equals(id));

            bankAccountRepository.save(bankAccount);
        });
    }

    @Override
    public void destroy(Long id) {
        this.bankAccountRepository.delete(this.getById(id));
    }
}
