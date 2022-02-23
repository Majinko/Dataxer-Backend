package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Todo;
import com.data.dataxer.models.dto.TodoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaskMapper {
    Todo taskDTOtoTask(TodoDTO taskDTO);

    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "project.categories", ignore = true)
    @Mapping(target = "user.roles", ignore = true)
    @Mapping(target = "userFrom.roles", ignore = true)
    TodoDTO taskToTaskDTO(Todo task);

    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "project.categories", ignore = true)
    @Mapping(target = "user.roles", ignore = true)
    @Mapping(target = "userFrom.roles", ignore = true)
    @Mapping(target = "files", ignore = true)
    TodoDTO taskToTaskDTOSimple(Todo task);

    @Mapping(target = "files", ignore = true)
    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "project.categories", ignore = true)
    @Mapping(target = "user.roles", ignore = true)
    @Mapping(target = "userFrom.roles", ignore = true)
    TodoDTO taskToTaskDTOPaginate(Todo task);
}
