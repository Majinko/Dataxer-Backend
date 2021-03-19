package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByIdAndCompanyId(Long id, Long companyId);

    Optional<BankAccount> findByIsDefaultAndCompanyId(Boolean isDefault, Long companyId);

    List<BankAccount> findAllByCompanyId(Long companyId);
}
