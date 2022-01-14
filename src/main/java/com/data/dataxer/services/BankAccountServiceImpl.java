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

            account.setCompany(bankAccount.getCompany());
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
        return this.bankAccountRepository.findAllByAppProfileId(SecurityUtils.defaultProfileId());
    }

    @Override
    public BankAccount getById(Long id) {
        return bankAccountRepository.findByIdAndAppProfileId(id, SecurityUtils.defaultProfileId()).orElseThrow(() -> new RuntimeException("Bank account not found"));
    }

    @Override
    public BankAccount getDefaultBankAccount(Long companyId) {
        return this.bankAccountRepository.findByIsDefaultAndCompanyIdAndAppProfileId(true, companyId, SecurityUtils.defaultProfileId()).orElseThrow(
                () -> new RuntimeException("Default account not found, please set it")
        );
    }

    @Override
    public void setDefaultBankAccount(Long id) {
        List<BankAccount> bankAccounts = bankAccountRepository.findAllByAppProfileId(SecurityUtils.defaultProfileId());

        bankAccounts.forEach(bankAccount -> {
            bankAccount.setIsDefault(bankAccount.getId().equals(id));
        });

        bankAccountRepository.saveAll(bankAccounts);
    }

    @Override
    public void destroy(Long id) {
        this.bankAccountRepository.delete(this.getById(id));
    }
}
