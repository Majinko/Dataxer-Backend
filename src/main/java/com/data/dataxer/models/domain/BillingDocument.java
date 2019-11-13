package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.BillingDocumentState;
import com.data.dataxer.models.enums.BillingDocumentType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class BillingDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    User user;

    @OneToOne
    Contact contact;

    @OneToOne
    Project project;

    String name;

    @Column(unique = true)
    String variable;

    @Enumerated
    BillingDocumentType type;

    BillingDocumentState state;

    String subject;

    @Column(columnDefinition = "text")
    String note;

    private Double price;

    private Double tax;

    @Column(columnDefinition = "double default 0")
    private Double discount;

    private LocalDateTime dueDate;

    private LocalDateTime sentDate;

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
