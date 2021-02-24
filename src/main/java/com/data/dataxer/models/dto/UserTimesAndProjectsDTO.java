package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserTimesAndProjectsDTO {
    private List<TimeDTO> userTimesForPeriod;

    private List<ProjectDTO> userUniqueProjects;
}
