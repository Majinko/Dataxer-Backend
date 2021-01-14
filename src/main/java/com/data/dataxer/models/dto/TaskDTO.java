package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.enums.DocumentState;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    DocumentState state;
    private boolean sendEmail;
    private LocalDateTime finishedAt;
    private List<StorageFileDTO> files = new ArrayList<>();
}
