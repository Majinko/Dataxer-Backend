package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Demand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DemandRepository extends JpaRepository<Demand, Long> {
    Optional<Demand> findByIdAndCompanyId(Long id, Long companyId);
}
