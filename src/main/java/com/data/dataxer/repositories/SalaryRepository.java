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

    Salary findByUserUidAndFinishIsNullAndAppProfileId(String uid, Long appProfileId);

    Optional<Salary> findByIdAndAppProfileId(Long id, Long appProfileId);

    List<Salary> findAllByUserUidAndAppProfileId(String userId, Sort sort, Long appProfileId);
}
