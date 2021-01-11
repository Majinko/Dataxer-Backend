package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class MailTemplates extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String emailSubject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String emailContent;

}
