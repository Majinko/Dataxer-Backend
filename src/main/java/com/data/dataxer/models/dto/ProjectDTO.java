package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.enums.ProjectState;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProjectDTO {
    private Long id;
    private Contact contact;
    private String title;
    private String number;
    private String description;
    private ProjectState state;
    private String address;
    private Float area;
    private Float projectProfit;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private List<CategoryDTO> categories = new ArrayList<>();
}
