package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.dto.ProjectDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ProjectMapper {
    Project projectDTOtoProject(ProjectDTO projectDTO);

    ProjectDTO projectToProjectDto(Project project);
}
