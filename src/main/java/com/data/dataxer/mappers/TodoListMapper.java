package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.TodoList;
import com.data.dataxer.models.dto.TodoListDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TodoListMapper {
    TodoList todoListDTOtoTodoList(TodoListDTO todoListDTO);

    TodoListDTO todoListToTodoListDTO(TodoList todoList);
}
