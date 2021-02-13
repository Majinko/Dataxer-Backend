package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
public class CategoryAble {
    @EmbeddedId
    @Column(insertable=false, updatable=false)
    private CategoryAbleId id = new CategoryAbleId();

    private Long categoryId;

    private Long categoryAbleId;

    private String categoryAbleType;
}
