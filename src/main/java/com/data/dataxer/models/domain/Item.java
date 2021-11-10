package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE item SET deleted_at = now() WHERE id = ?")
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "item")
    private List<ItemPrice> itemPrices = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact supplier;

    @JoinTable(
            name = "storage",
            joinColumns = @JoinColumn(name = "fileAbleId"),
            inverseJoinColumns = @JoinColumn(name = "id"),
            foreignKey = @javax.persistence.ForeignKey(name = "none")
    )
    @OneToMany(fetch = FetchType.LAZY)
    @Where(clause = "file_able_type='item'")
    private List<Storage> files = new ArrayList<>();

    String previewUrl;
    String title;
    String type;

    @Column(columnDefinition = "text")
    String shortDescription;

    @Column(columnDefinition = "text")
    String description;

    String manufacturer;

    @Column(columnDefinition = "text")
    String web;
    String unit;

    // represent code of item on the other web
    String code;
    String dimensions;

    Boolean isPartOfSet;
    Boolean needMontage;

    String priceLevel;

    // model of items, v1, v2...
    String model;
    String series;
    String color;
    String material;

    private LocalDateTime deletedAt;
}
