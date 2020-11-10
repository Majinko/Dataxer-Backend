package com.data.dataxer.services;

import com.data.dataxer.models.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {
    Project store(Project project);

    Project update(Project project);

    Project getById(Long id);

    Page<Project> paginate(Pageable pageable);

    void destroy(Long id);

    List<Project> all();

    List<Project> search(String queryString);
}
