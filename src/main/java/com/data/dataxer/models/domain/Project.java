package com.data.dataxer.models.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Project extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @JoinColumn(name = "client_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Contact contact;

    @OneToOne
    private AppUser user;

    private String title;

    private String number;

    @Column(columnDefinition = "text")
    private String description;

    private String state;

    private String address;

    private Float area;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;
}
