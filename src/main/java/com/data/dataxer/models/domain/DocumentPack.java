package com.data.dataxer.models.domain;

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
public class DocumentPack implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentPackId;

    private Long documentId;

    private String type;

    @OneToMany(mappedBy = "pack", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<DocumentPackItem> packItems = new ArrayList<>();

    Integer position;

    String title;

    Integer tax;

    private BigDecimal totalPrice;

}
