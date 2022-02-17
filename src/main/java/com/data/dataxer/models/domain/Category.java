package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.CategoryGroup;
import com.data.dataxer.models.enums.CategoryType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE category SET deleted_at = now() WHERE id = ?")
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long parentId;

    @NotNull
    @Audited
    private String name;

    private Integer depth;

    private Integer position;

    @Enumerated(EnumType.STRING)
    private CategoryGroup categoryGroup;

    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    private LocalDateTime deletedAt;

    @Transient
    private List<Category> children = new ArrayList<>();
}
