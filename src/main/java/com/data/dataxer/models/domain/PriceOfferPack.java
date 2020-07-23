package com.data.dataxer.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class PriceOfferPack implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_offer_id", referencedColumnName = "id")
    PriceOffer priceOffer;

    @OneToMany(mappedBy = "priceOfferPack", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<PriceOfferPackItem> items = new ArrayList<>();

    Integer position;

    String title;

    Integer tax;

    private BigDecimal totalPrice;
}
