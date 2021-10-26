package com.data.dataxer.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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

    @JsonIgnore
    @OneToMany(mappedBy = "contact", fetch = FetchType.LAZY)
    private List<Project> projects = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    private Category category;

    @NotNull
    private String name;

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

    @Column(length = 50)
    private String cin;

    private String tin;

    private String vatin;

    private LocalDateTime deletedAt;
}
