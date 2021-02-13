package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.ProjectState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE project SET deleted_at = now() WHERE id = ?")
public class Project extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "client_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Contact contact;

    @OneToOne
    private AppUser user;

    @JoinTable(
            name = "categoryAble",
            joinColumns = @JoinColumn(name = "categoryAbleId"),
            inverseJoinColumns = @JoinColumn(name = "categoryId"),
            foreignKey = @javax.persistence.ForeignKey(name = "none")
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @Where(clause="categoryAbleType='project'")
    private List<Category> categories = new ArrayList<>();

    private String title;

    private String number;

    @Column(columnDefinition = "text")
    private String description;

    private ProjectState state;

    private String address;

    private Float area;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private LocalDateTime deletedAt;
}
