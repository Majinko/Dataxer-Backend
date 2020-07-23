package com.data.dataxer.models.dto;

import com.data.dataxer.models.domain.Contact;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProjectDTO {
    private Long id;
    private Contact contact;
    private String title;
    private String number;
    private String description;
    private String state;
    private String address;
    private Float area;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}
