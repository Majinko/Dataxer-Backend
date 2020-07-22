package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskDTO {
    private Long id;
    private Project project;
    private Category category;
    private AppUser user;
    private AppUser userFrom;
    private String title;
    private String description;
    private String completion;
    private String state;
    private boolean sendEmail;
    private LocalDateTime finishedAt;
}
