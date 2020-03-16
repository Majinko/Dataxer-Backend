package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.annotation.PreDestroy;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE contact SET deleted_at = now() WHERE id = ?")
public class Contact extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "contact", fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

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
}
