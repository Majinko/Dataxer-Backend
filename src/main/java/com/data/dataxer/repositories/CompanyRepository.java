package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
