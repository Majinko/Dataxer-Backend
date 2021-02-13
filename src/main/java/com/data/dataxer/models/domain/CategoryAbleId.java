package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CategoryAbleId implements Serializable {
    private Long categoryId;

    private Long categoryAbleId;

    private String categoryAbleType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryAbleId)) return false;
        CategoryAbleId that = (CategoryAbleId) o;
        return Objects.equals(getCategoryId(), that.getCategoryId()) &&
                Objects.equals(getCategoryAbleId(), that.getCategoryAbleId()) &&
                Objects.equals(getCategoryAbleType(), that.getCategoryAbleType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCategoryId(), getCategoryAbleId(), getCategoryAbleType());
    }
}
