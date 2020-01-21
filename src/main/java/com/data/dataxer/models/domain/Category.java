package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.PreDestroy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @OneToOne
    private Category parent;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private List<Category> child = new ArrayList<>();

    private LocalDateTime deletedAt;

    @PreDestroy
    private void destroy() {
        deletedAt = LocalDateTime.now();
    }
}
