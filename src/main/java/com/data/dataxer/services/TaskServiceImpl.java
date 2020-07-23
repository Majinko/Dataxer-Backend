package com.data.dataxer.services;

import com.data.dataxer.models.domain.Task;
import com.data.dataxer.repositories.TaskRepository;
import com.data.dataxer.repositories.qrepositories.QTaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final QTaskRepository qTaskRepository;

    public TaskServiceImpl(TaskRepository taskRepository, QTaskRepository qTaskRepository) {
        this.taskRepository = taskRepository;
        this.qTaskRepository = qTaskRepository;
    }

    @Override
    public void store(Task task) {
        this.taskRepository.save(task);
    }

    @Override
    public void update(Task task) {
        this.taskRepository.save(task);
    }

    @Override
    public Task getById(Long id) {
        return null;
    }

    @Override
    public Page<Task> paginate(Pageable pageable) {
        return null;
    }

    @Override
    public void destroy(Long id) {

    }
}
