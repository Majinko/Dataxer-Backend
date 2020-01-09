package com.data.dataxer.models.domain;

import com.data.dataxer.securityContextUtils.SecurityContextUtils;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
abstract class BaseEntity implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created", referencedColumnName = "uid", updatable = false, nullable = true)
    private DataxerUser created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated", referencedColumnName = "uid", nullable = false)
    private DataxerUser updated;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PreUpdate
    private void update() {
        updated = SecurityContextUtils.loggedUser();
    }

    @PrePersist
    private void persist() {
        if (SecurityContextHolder.getContext().getAuthentication() != null)
            try {
                created = SecurityContextUtils.loggedUser();
            } catch (NullPointerException ex) {
                created = null;
            }

    }
}
