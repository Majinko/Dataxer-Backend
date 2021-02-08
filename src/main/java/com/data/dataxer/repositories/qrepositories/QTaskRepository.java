package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QTaskRepository {
    Task getById(Long id, Long companyId);

    Page<Task> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId);
}
