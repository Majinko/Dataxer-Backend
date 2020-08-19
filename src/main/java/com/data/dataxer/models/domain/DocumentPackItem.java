package com.data.dataxer.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class DocumentPackItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_pack_id", referencedColumnName = "id")
    DocumentPack pack;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    private Float qty;

    private String title;

    Integer position;

    private BigDecimal discount;

    private BigDecimal price;

    private Integer tax;

    private BigDecimal totalPrice;

    public DocumentPackItem() {}

    public DocumentPackItem(DocumentPackItem packItem, DocumentPack pack) {
        this.pack = pack;
        this.item = packItem.getItem();
        this.qty = packItem.getQty();
        this.title = packItem.getTitle();
        this.position = packItem.getPosition();
        this.discount = packItem.getDiscount();
        this.price = packItem.getPrice();
        this.tax = packItem.getTax();
        this.totalPrice = packItem.getTotalPrice();
    }

}
