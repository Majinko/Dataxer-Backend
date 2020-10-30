package com.data.dataxer.models.domain;

import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE contact SET deleted_at = now() WHERE id = ?")
public abstract class BaseEntitySoftDelete implements Serializable {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
    private Company company;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime deleteAt;

    @PrePersist
    private void persist() {
        if (SecurityContextHolder.getContext().getAuthentication() != null)
            try {
                company = SecurityUtils.defaultCompany();
            } catch (NullPointerException ex) {
                company = null;
            }
    }
}
