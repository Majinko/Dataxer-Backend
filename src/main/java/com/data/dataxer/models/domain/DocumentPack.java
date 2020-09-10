package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DocumentType;
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
    private Long id;

    private Long documentId;

    private Enum<DocumentType> type;

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<DocumentPackItem> packItems = new ArrayList<>();

    Integer position;

    String title;

    Integer tax;

    private BigDecimal totalPrice;

    public DocumentPack() {}

    public DocumentPack(DocumentPack pack) {
        this.documentId = null;
        this.type = pack.getType();
        this.position = pack.getPosition();
        this.title = pack.getTitle();
        this.tax = pack.getTax();
        this.totalPrice = pack.getTotalPrice();
        if (pack.getPackItems() != null && !pack.getPackItems().isEmpty()) {
            this.setDuplicatedDocumentPackItems(pack.getPackItems(), this);
        }
    }

    private void setDuplicatedDocumentPackItems(List<DocumentPackItem> packItems, DocumentPack pack) {
        for (DocumentPackItem packItem : packItems) {
            this.packItems.add(new DocumentPackItem(packItem, pack));
        }
    }
}
