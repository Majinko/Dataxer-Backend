package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Todo;
import com.data.dataxer.models.domain.TodoList;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TodoRepository extends CrudRepository<Todo, Long> {
    @Query("select todo from Todo todo left join fetch todo.todoList left join fetch todo.assignedUser left join fetch todo.fromUser where todo.todoList in ?1 and todo.appProfile.id = ?2")
    List<Todo> findAllByTodoListInAndAppProfileId(List<TodoList> todoLists, Long appProfileId);
}
