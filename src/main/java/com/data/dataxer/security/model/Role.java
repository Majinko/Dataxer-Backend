package com.data.dataxer.security.model;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.DataxerUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @ManyToMany(mappedBy = "roles")
    private Collection<DataxerUser> users = new ArrayList<DataxerUser>();

    @ManyToMany
    private Collection<Privilege> privileges = new ArrayList<Privilege>();
}
