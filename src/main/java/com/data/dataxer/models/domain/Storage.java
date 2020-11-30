package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
public class Storage extends BaseEntitySoftDelete {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fileAbleId;

    private String fileAbleType;

    private String fileName;

    private String hashFileName;

    private String ext;

    private String path;

    private Boolean isDefault;

    private Long size;
}
