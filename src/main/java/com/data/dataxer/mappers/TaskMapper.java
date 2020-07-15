package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Task;
import com.data.dataxer.models.dto.TaskDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TaskMapper {
    Task taskDTOtoTask(TaskDTO taskDTO);

    TaskDTO taskToTaskDTO(Task task);
}
