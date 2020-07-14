package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QProjectRepository {
    Page<Project> paginate(Pageable pageable, List<Long> companyIds);

    Project getById(Long id, List<Long> companyIds);
}
