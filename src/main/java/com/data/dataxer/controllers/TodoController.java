package com.data.dataxer.controllers;

import com.data.dataxer.mappers.TodoListMapper;
import com.data.dataxer.mappers.TodoMapper;
import com.data.dataxer.models.dto.TodoDTO;
import com.data.dataxer.models.dto.TodoListDTO;
import com.data.dataxer.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    @Autowired
    TodoService todoService;

    @Autowired
    TodoListMapper todoListMapper;

    @Autowired
    TodoMapper todoMapper;

    @GetMapping("/todolist/{todoListId}")
    public ResponseEntity<TodoListDTO> getTodoListById(
            @PathVariable Long todoListId
    ) {
        return ResponseEntity.ok(this.todoListMapper.todoListToTodoListDTO(todoService.getTodoListById(todoListId)));
    }

    @GetMapping("/allTodoList")
    public ResponseEntity<List<TodoListDTO>> all() {
        return ResponseEntity.ok(this.todoService.all().stream().map(todoList -> todoListMapper.todoListToTodoListDTO(todoList)).collect(Collectors.toList()));
    }

    @PostMapping("/storeTodoList")
    public void storeTodoList(@RequestBody TodoListDTO todoList) {
        this.todoService.storeTodoList(todoListMapper.todoListDTOtoTodoList(todoList));
    }

    @PostMapping("/updateTodoList")
    public void updateTodoList(@RequestBody TodoListDTO todoList) {
        this.todoService.updateTodoList(todoListMapper.todoListDTOtoTodoList(todoList));
    }

    @PostMapping("/storeTodo/{todoListId}")
    public void storeTodo(
            @PathVariable Long todoListId,
            @RequestBody TodoDTO todo
    ) {
        this.todoService.storeTodo(todoListId, todoMapper.todoDTOtoTodo(todo));
    }
}
