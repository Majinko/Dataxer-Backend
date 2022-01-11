package com.data.dataxer.models.domain;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

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

    @JoinTable(
            name = "app_user_profiles",
            joinColumns = @JoinColumn(name = "app_profile_id"),
            inverseJoinColumns = @JoinColumn(name = "app_user_id")
    )
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<AppUser> appUsers = new ArrayList<>();
}
