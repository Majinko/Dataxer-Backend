package com.data.dataxer.controllers;

import com.data.dataxer.mappers.TodoListMapper;
import com.data.dataxer.models.dto.TodoListDTO;
import com.data.dataxer.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todo")
public class TodoController {
    @Autowired
    TodoService todoService;

    @Autowired
    TodoListMapper todoListMapper;

    @GetMapping("/allTodoList")
    public ResponseEntity<List<TodoListDTO>> all() {
        return ResponseEntity.ok(this.todoService.all().stream().map(todoList -> todoListMapper.todoListToTodoListDTO(todoList)).collect(Collectors.toList()));
    }
}
