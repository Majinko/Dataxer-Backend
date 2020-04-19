package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.DataxerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByDataxerUserId(Long userId);

    Optional<Company> findAllByIdAndDataxerUserId(Long companyId, Long userId);
}