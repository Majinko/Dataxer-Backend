package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DocumentState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE item SET deleted_at = now() WHERE id = ?")
public class Demand extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact contact;

    String title;

    @Column(columnDefinition = "text")
    String description;

    String source;

    @Enumerated(EnumType.STRING)
    DocumentState state;

    private LocalDateTime deletedAt;
}
