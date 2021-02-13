package com.data.dataxer.security.model;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@Setter
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Collection<AppUser> users = new ArrayList<AppUser>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<Privilege> privileges = new ArrayList<Privilege>();
}
