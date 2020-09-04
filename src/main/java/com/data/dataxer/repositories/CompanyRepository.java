package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByAppUserId(Long userId);

    Optional<Company> findAllByIdAndAppUserId(Long companyId, Long userId);

    Optional<Company> findByDefaultCompanyAndAppUserId(boolean isDefault, Long userId);
}
