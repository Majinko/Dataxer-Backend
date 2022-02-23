package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TodoListDTO {
    private Long id;
    private Integer position;
    private String title;
    private boolean isPrivate;
    private List<TodoDTO> todos;
}
