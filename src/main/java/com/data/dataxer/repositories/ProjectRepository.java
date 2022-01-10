package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByAppProfileId(Long appProfileId);
    List<Project> findAllByContactIdAndAppProfileId(Long contactId, Long appProfileId);
}
