package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.dto.ProjectDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface ProjectMapper {
    Project projectDTOtoProject(ProjectDTO projectDTO);

    ProjectDTO projectToProjectDTO(Project project);

    @Named(value = "projectToProjectDTOSimple")
    @Mapping(target = "contact", ignore = true)
    @Mapping(target = "categories", ignore = true)
    ProjectDTO projectToProjectDTOSimple(Project project);

    @Named(value = "projectToProjectDTOWithoutCategory")
    @Mapping(target = "categories", ignore = true)
    ProjectDTO projectToProjectDTOWithoutCategory(Project project);

    @IterableMapping(qualifiedByName = "projectToProjectDTOSimple")
    List<ProjectDTO> projectToProjectDTOs(List<Project> projects);
}
