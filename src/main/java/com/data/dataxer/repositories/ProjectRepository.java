package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
