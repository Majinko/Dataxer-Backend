package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Document {
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
    DocumentType type;

    DocumentState state;

    String subject;

    @Column(columnDefinition = "text")
    String note;

    @DecimalMin(value = "0.00")
    private BigDecimal price;

    private Integer tax;

    @Column(name = "discount", precision=8, scale=2)
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
