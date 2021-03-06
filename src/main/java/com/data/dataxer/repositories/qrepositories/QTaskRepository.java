package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QTaskRepository {
    Task getById(Long id, List<Long> companyIds);

    Page<Task> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds);
}
