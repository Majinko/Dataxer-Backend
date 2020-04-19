package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ItemPrice> itemPrices = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact supplier;

    String title;

    String type;

    @Column(columnDefinition = "text")
    String shortDescription;

    @Column(columnDefinition = "text")
    String description;

    String manufacturer;

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
