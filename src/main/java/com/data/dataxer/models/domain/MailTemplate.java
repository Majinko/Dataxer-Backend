
package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.MailTemplateType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE mail_templates SET deleted_at = now() WHERE id = ?")
public class MailTemplate extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
    private Company company;

    @Column(nullable = false)
    private String emailSubject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String emailContent;

    @NotNull
    @Enumerated(EnumType.STRING)
    MailTemplateType mailTemplateType;

    private LocalDateTime deletedAt;
}
