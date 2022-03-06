package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TodoListDTO {
    private Long id;
    private Integer position;
    private String title;
    private String note;
    private Boolean isPrivate;
    private List<TodoDTO> todos;
    private LocalDateTime createdAt;
}
