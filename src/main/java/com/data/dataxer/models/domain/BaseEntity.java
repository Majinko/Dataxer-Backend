package com.data.dataxer.models.domain;

import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_profile_id", referencedColumnName = "id", updatable = false)
    private AppProfile appProfile;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    private void persist() {
        if (SecurityContextHolder.getContext().getAuthentication() != null)
            try {
                appProfile = SecurityUtils.defaultProfile();
            } catch (NullPointerException ex) {
                appProfile = null;
            }
    }
}
