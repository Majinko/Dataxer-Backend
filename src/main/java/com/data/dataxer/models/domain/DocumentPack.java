package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DocumentType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Setter
@Getter
public class DocumentPack implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private DocumentBase document;

    private Enum<DocumentType> type;

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<DocumentPackItem> packItems = new ArrayList<>();

    Integer position;

    String title;

    Integer tax;

    private Boolean customPrice;

    private BigDecimal price;

    private BigDecimal totalPrice;
}
