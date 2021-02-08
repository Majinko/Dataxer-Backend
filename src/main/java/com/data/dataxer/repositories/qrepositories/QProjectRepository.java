package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QProjectRepository {

    Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId, Boolean disableFilter);

    Project getById(Long id, Long companyId, Boolean disableFilter);

    List<Project> search(String queryString, Long companyId, Boolean disableFilter);
}
