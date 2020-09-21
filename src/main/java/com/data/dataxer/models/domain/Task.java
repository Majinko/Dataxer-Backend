package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DocumentState;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Task extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "project_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @JoinColumn(name = "catergory_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Category category;

    @JoinColumn(name = "user_uid", referencedColumnName = "uid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @JoinColumn(name = "user_from_uid", referencedColumnName = "uid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser userFrom;

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private String completion;

    DocumentState state;

    private LocalDateTime finishedAt;
}
