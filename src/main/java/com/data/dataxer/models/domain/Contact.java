package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.PreDestroy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Contact extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String photoUrl;

    private String street;

    private String city;

    private String country;

    private String postalCode;

    private String regNumber;

    private String email;

    private String phone;

    @Column(columnDefinition = "text")
    private String note;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "contact", fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

    @PreDestroy
    private void destroy() {
        deletedAt = LocalDateTime.now();
    }
}
