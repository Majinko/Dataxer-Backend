package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
