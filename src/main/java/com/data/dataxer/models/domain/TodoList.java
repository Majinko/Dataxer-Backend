package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class TodoList extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer position;

    private String title;

    @Column(nullable = false)
    private boolean isPrivate = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "todo")
    private List<Todo> todos = new ArrayList<>();
}
