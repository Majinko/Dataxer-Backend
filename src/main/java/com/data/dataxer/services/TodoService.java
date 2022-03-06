package com.data.dataxer.services;

import com.data.dataxer.models.domain.Todo;
import com.data.dataxer.models.domain.TodoList;

import java.util.List;

public interface TodoService {
    TodoList getTodoListById(Long todoListId);

    List<TodoList> all();

    void storeTodoList(TodoList todoList);

    void updateTodoList(TodoList todoList);

    void storeTodo(Long todoListId, Todo todo);
}
