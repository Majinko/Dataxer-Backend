package com.data.dataxer.models.domain;

import com.data.dataxer.security.model.Role;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "app_user")
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false, length = 100)
    private String uid;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Company> companies = new ArrayList<>();

    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false, updatable = false, length = 50)
    private String email;

    private String phone;

    private String street;

    private String city;

    private String postalCode;

    private String country;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;
}
