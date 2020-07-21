package com.data.dataxer.services;

import com.data.dataxer.models.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    void store(Task task);

    void update(Task task);

    Task getById(Long id);

    Page<Task> paginate(Pageable pageable);

    void destroy(Long id);
}