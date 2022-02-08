package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("select p from Project p where p.appProfile.id = ?1")
    List<Project> findAllByAppProfileId(Long appProfileId);
    List<Project> findAllByContactIdAndAppProfileId(Long contactId, Long appProfileId);
}
