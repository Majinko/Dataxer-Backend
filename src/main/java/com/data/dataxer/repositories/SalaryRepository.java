package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Page<Salary> findAllByUserId(Pageable pageable, Long userId);

    Salary findByUserUidAndFinishIsNullAndCompanyId(String uid, Long companyId);

    Optional<Salary> findByIdAndAndCompanyId(Long id, Long companyId);

    List<Salary> findAllByUserUidAndCompanyId(String userId, Sort sort, Long companyId);
}
