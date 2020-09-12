package com.data.dataxer.models.domain;

import com.data.dataxer.mappers.HashMapConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

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
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="DISCRIMINATOR", discriminatorType= DiscriminatorType.STRING)
@DiscriminatorValue("DOCUMENT")
@Table(name="BASIC_DOCUMENT")
public class DocumentBase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected String id;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    List<DocumentPack> packs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact contact;

    private String title;

    private String number;

    private String state;

    @Column(columnDefinition = "text")
    private String note;

    private BigDecimal price;

    private BigDecimal totalPrice;

    @Column(columnDefinition = "text")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> documentData;

    private LocalDate createdDate;

    private LocalDate deliveredDate;

    private LocalDate dueDate;

    private LocalDateTime deletedAt;

}
