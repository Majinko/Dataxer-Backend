package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.BankAccount;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByIdAndCompanyIdIn(Long id, List<Long> companyIds);

    Optional<BankAccount> findByIsDefaultAndCompanyIdIn(Boolean isDefault, List<Long> companyIds);

    List<BankAccount> findAllByCompanyIdIn(List<Long> companyIds);
}
