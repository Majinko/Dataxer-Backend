package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Task;
import com.data.dataxer.models.dto.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface TaskMapper {
    Task taskDTOtoTask(TaskDTO taskDTO);

    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "user.companies", ignore = true)
    @Mapping(target = "user.roles", ignore = true)
    @Mapping(target = "userFrom.roles", ignore = true)
    @Mapping(target = "userFrom.companies", ignore = true)
    TaskDTO taskToTaskDTO(Task task);

    @Mapping(target = "files", ignore = true)
    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "user.companies", ignore = true)
    @Mapping(target = "user.roles", ignore = true)
    @Mapping(target = "userFrom.roles", ignore = true)
    @Mapping(target = "userFrom.companies", ignore = true)
    TaskDTO taskToTaskDTOPaginate(Task task);
}
