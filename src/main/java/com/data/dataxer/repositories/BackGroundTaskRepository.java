package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.BackGroundTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BackGroundTaskRepository extends JpaRepository<BackGroundTask, Long> {

    @Query("SELECT bt FROM BackGroundTask bt WHERE bt.name = ?1 AND bt.company.id = ?2")
    BackGroundTask findBackGroundTaskByNameAndCompanyId(String name, Long companyId);

}
