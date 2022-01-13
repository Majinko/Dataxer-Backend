package com.data.dataxer.models.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class AppProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "text")
    private String logoUrl;

    @JoinTable(
            name = "app_user_profiles",
            joinColumns = @JoinColumn(name = "app_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "app_user_id")
    )
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<AppUser> appUsers = new ArrayList<>();
}
