package com.data.dataxer.models.domain;

import com.data.dataxer.mappers.HashMapConverter;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("DOCUMENT")
@Table(name = "DOCUMENT_BASE")
@Where(clause = "deleted_at is null")
public abstract class DocumentBase extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "document")
    List<DocumentPack> packs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    Project project;

    protected String title;

    protected String subject;

    protected String number;

    @Enumerated(EnumType.STRING)
    protected DocumentState state;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Column(columnDefinition = "text")
    protected String note;

    protected BigDecimal discount;

    protected BigDecimal price;

    protected BigDecimal totalPrice;

    @Formula("price - (price * (discount / 100))")
    private BigDecimal priceAfterDiscount;

    @Column(columnDefinition = "text")
    @Convert(converter = HashMapConverter.class)
    protected Map<String, Object> documentData;

    protected LocalDate createdDate;

    protected LocalDate deliveredDate;

    protected LocalDate dueDate;

    protected LocalDateTime deletedAt;

    public abstract BigDecimal countDiscountTotalPrice();

    public abstract BigDecimal countTaxPrice(BigDecimal price, Integer tax);
}


