package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.CategoryType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
public class Category extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private Integer lft;

    private Integer rgt;

    private Integer depth;

    @Enumerated(EnumType.STRING)
    CategoryType categoryType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @OrderBy("lft")
    private List<Category> children = new ArrayList<Category>();

    private LocalDateTime deletedAt;
}
