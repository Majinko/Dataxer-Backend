package com.data.dataxer.services;

import com.data.dataxer.models.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    Task store(Task task);

    void update(Task task);

    Task getById(Long id, Boolean disableFilter);

    Page<Task> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter);

    void destroy(Long id);
}
