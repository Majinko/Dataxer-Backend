package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TodoDTO {
    private Long id;
    private AppUserDTO fromUser;
    private AppUserDTO assignedUser;
    private String title;
    private String note;
    private LocalDateTime createdAt;
}
