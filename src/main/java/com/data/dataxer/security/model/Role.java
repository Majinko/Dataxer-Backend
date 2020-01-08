package com.data.dataxer.security.model;

import com.data.dataxer.models.domain.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users = new ArrayList<User>();

    @ManyToMany
    private Collection<Privilege> privileges = new ArrayList<Privilege>();
}
