package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.ItemPriceLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
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
    private Company company;

    // represent code of item on the other web
    String code;

    String title;

    // model of items, v1, v2...
    String model;

    String manufacturer;

    ItemPriceLevel itemPriceLevel;

    String timeDelivery;

    @Column(columnDefinition = "text")
    String text;

    @OneToMany(mappedBy = "item", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ItemPrice> itemPrices = new HashSet<>();

    @ManyToMany
    private List<Category> categories = new ArrayList<Category>();

    private LocalDateTime deletedAt;
}
