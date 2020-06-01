package com.data.dataxer.models.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Project extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @JoinColumn(name = "contact_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Contact contact;

    @OneToOne
    AppUser user;

    String name;

    String number;
}
