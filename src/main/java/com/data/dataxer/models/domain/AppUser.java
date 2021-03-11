package com.data.dataxer.models.domain;

import com.data.dataxer.security.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "app_user")
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE category SET deleted_at = now() WHERE id = ?")
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false, length = 100)
    private String uid;

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
    private List<Role> roles = new ArrayList<>();

    @JsonIgnore
    @JoinTable(
            name = "app_user_companies",
            joinColumns = @JoinColumn(name = "app_user_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id")
    )
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Company> companies = new ArrayList<>();

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;
}
