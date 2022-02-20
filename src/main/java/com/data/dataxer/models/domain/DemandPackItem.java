package com.data.dataxer.models.domain;

import javax.persistence.*;

@Entity
public class DemandPackItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private DemandPack demandPack;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @OneToOne(fetch = FetchType.LAZY)
    private Category category;

    private String title;

    private String unit;

    private Float qty;

    private int position;
}
