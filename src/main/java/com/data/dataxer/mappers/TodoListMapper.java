package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Todo;
import com.data.dataxer.models.domain.TodoList;
import com.data.dataxer.models.dto.TodoDTO;
import com.data.dataxer.models.dto.TodoListDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TodoListMapper {
    TodoList todoListDTOtoTodoList(TodoListDTO todoListDTO);

    TodoListDTO todoListToTodoListDTO(TodoList todoList);

    @Mapping(target = "fromUser.roles", ignore = true)
    @Mapping(target = "assignedUser.roles", ignore = true)
    TodoDTO todoToTodoDTO(Todo todo);
}
