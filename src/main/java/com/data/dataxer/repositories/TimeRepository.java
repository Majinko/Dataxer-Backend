package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Time;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimeRepository extends JpaRepository<Time, Long> {
    List<Time> findByCategoryIdIn(List<Long> ids);

    Time findFirstByUserIdAndCompanyIdAndDateWorkOrderByIdDesc(Long userId, Long companyId, LocalDate dateWork);

    List<Time> findAllBySalaryIdAndAndCompanyId(Long salaryId, Long companyId);
}
