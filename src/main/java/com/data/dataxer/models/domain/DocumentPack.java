package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DocumentType;
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

    private Long documentId;

    private Enum<DocumentType> type;

    @OneToMany(mappedBy = "pack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<DocumentPackItem> packItems = new ArrayList<>();

    Integer position;

    String title;

    Integer tax;

    private BigDecimal price;

    private BigDecimal totalPrice;

    public DocumentPack() {}

    public DocumentPack(DocumentPack pack) {
        this.documentId = null;
        this.type = pack.getType();
        this.position = pack.getPosition();
        this.title = pack.getTitle();
        this.tax = pack.getTax();
        this.price = pack.getPrice();
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

    public Map<Integer, BigDecimal> getPackItemsTaxesAndValues() {
        Map<Integer, BigDecimal> taxesAndValues = new HashMap<>();
        for (DocumentPackItem item: this.packItems) {
            if (taxesAndValues.containsKey(item.getTax())) {
                BigDecimal value = taxesAndValues.get(item.getTax());
                value = value.add(item.getPrice());
                taxesAndValues.replace(item.getTax(), value);
            } else {
                taxesAndValues.put(item.getTax(), item.getPrice());
            }
        }
        return taxesAndValues;
    }
}
