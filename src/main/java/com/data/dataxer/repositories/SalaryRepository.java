package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Page<Salary> findAllByUserId(Pageable pageable, Long userId);
}
