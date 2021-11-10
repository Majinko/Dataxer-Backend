package com.data.dataxer.models.dto;

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
    private ProjectDTO project;
    private CategoryDTO category;
    private AppUserDTO user;
    private AppUserDTO userFrom;
    private String title;
    private String description;
    private String completion;
    private DocumentState state;
    private boolean sendEmail;
    private LocalDateTime finishedAt;
    private List<StorageFileDTO> files = new ArrayList<>();
}
