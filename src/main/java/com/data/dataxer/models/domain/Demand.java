package com.data.dataxer.models.domain;

import com.data.dataxer.mappers.HashMapConverter;
import com.data.dataxer.models.enums.DocumentState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Setter
@Getter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE item SET deleted_at = now() WHERE id = ?")
public class Demand extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    Project project;

    @ManyToMany(fetch = FetchType.LAZY)
    List<Contact> contacts;
    
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "demand")
    private List<DemandPack> packs;

    @Column(columnDefinition = "text")
    @Convert(converter = HashMapConverter.class)
    Map<String, Object> documentData;

    String title;

    @Column(columnDefinition = "text")
    String description;

    String source;

    String subject;

    @Column(columnDefinition = "text")
    String note;

    @Enumerated(EnumType.STRING)
    DocumentState state;

    LocalDate createdDate;

    LocalDate deliveredDate;

    LocalDate dueDate;

    LocalDateTime deletedAt;
}
