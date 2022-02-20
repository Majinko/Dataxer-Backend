package com.data.dataxer.models.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DemandPack extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    private Demand demand;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "demandPack")
    private List<DemandPackItem> demandPackItems = new ArrayList<>();

    private String title;

    private boolean showItems;
}
