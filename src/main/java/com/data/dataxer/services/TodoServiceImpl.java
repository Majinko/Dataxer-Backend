package com.data.dataxer.services;

import com.data.dataxer.models.domain.Todo;
import com.data.dataxer.models.domain.TodoList;
import com.data.dataxer.repositories.TodoListRepository;
import com.data.dataxer.repositories.TodoRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoServiceImpl implements TodoService {
    @Autowired
    private TodoListRepository todoListRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Override
    public TodoList getTodoListById(Long todoListId) {
        TodoList todoList = this.todoListRepository.findByIdAndAppProfileId(todoListId, SecurityUtils.defaultProfileId());

        todoList.setTodos(
                todoRepository.findAllByTodoListInAndAppProfileId(List.of(todoList), SecurityUtils.defaultProfileId())
        );

        return todoList;
    }

    @Override
    public List<TodoList> all() {
        List<TodoList> todoLists = this.todoListRepository.findAllByAppProfileIdOrderByPosition(SecurityUtils.defaultProfileId());

        List<Todo> todos = this.todoRepository.findAllByTodoListInAndAppProfileId(todoLists, SecurityUtils.defaultProfileId());

        // merge todolist and todos
        todoLists.forEach(todoList -> {
            todoList.setTodos(
                    todos.stream().filter(
                            todo -> todo.getTodoList().getId().equals(todoList.getId())
                    ).collect(Collectors.toList())
            );
        });

        return todoLists;
    }

    @Override
    public void storeTodoList(TodoList todoList) {
        this.todoListRepository.save(todoList);
    }

    @Override
    public void updateTodoList(TodoList todoList) {
        this.todoListRepository.save(todoList);
    }

    @Override
    public void storeTodo(Long todoListId, Todo todo) {
        todo.setTodoList(todoListRepository.findByIdAndAppProfileId(todoListId, SecurityUtils.defaultProfileId()));

        this.todoRepository.save(todo);
    }
}
