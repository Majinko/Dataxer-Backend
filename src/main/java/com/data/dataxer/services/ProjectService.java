package com.data.dataxer.services;

import com.data.dataxer.models.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    void store(Project project);

    void update(Project project);

    Project getById(Long id);

    Page<Project> paginate(Pageable pageable);

    void destroy(Long id);
}
