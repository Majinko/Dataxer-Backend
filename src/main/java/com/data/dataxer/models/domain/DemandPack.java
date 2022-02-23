package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class DemandPack extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    private Demand demand;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "demandPack")
    private List<DemandPackItem> demandPackItems = new ArrayList<>();

    private String title;

    private Integer position;

    private boolean showItems;
}
