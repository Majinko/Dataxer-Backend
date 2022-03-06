package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.TodoList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TodoListRepository extends CrudRepository<TodoList, Long> {
    List<TodoList> findAllByAppProfileIdOrderByPosition(Long profileId);

    TodoList findByIdAndAppProfileId(Long id, Long profileId);
}
