package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Todo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_from_uid", referencedColumnName = "uid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser fromUser;

    @JoinColumn(name = "user_to_uid", referencedColumnName = "uid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser assignedUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Todo todo;

    private String title;

    private String note;
}
