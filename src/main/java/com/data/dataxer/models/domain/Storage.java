package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Storage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fileAbleId;

    private String fileAbleType;

    private String fileName;

    private String hashFileName;

    private String contentType;

    private String path;

    private Boolean isDefault;

    private Long size;

    @Transient
    private byte[] content;
}
