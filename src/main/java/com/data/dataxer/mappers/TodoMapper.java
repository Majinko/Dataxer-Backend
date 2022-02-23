package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Todo;
import com.data.dataxer.models.dto.TodoDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TodoMapper {
    Todo todoDTOtoTodo(TodoDTO todoDTO);

    TodoDTO todoToTodoDTO(Todo todo);
}
