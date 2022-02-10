package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.AuditLogAction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class AuditLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AuditLogAction auditLogAction;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    private AppUser initiator;

    private Long objectId;

    @Column(columnDefinition = "TEXT")
    private String loggedObject;
}
