package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByIdAndAppProfileId(Long id, Long appProfileId);

    Optional<BankAccount> findByIsDefaultAndCompanyId(Boolean isDefault, Long companyId);

    List<BankAccount> findAllByAppProfileId(Long appProfileId);
}
