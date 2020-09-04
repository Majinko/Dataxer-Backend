package com.data.dataxer.models.domain;

import com.data.dataxer.mappers.HashMapConverter;
import com.data.dataxer.models.enums.DocumentState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Setter
@Getter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE invoice SET deleted_at = now() WHERE id = ?")
public class Invoice extends  BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<DocumentPack> packs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact contact;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String number;

    private DocumentState.InvoiceStates state;

    @Column(columnDefinition = "text")
    private String note;

    private BigDecimal price;

    private BigDecimal totalPrice;

    @Column(columnDefinition = "text")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> invoiceData;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Column(nullable = false)
    private LocalDate deliveredDate;

    //represent date when invoice was changed to payed state
    private LocalDate paymentDate;

    //represent date when invoice should be payed
    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDateTime deletedAt;

    public Invoice() {}
    public Invoice(Invoice invoice) {
        if (invoice.getPacks() != null && !invoice.getPacks().isEmpty()){
            this.duplicatePacks(invoice.getPacks());
        }
        this.contact = invoice.getContact();
        this.title = invoice.getTitle();
        this.number = invoice.getNumber();
        this.state = invoice.getState();
        this.note = invoice.getNote();
        this.price = invoice.getPrice();
        this.totalPrice = invoice.getTotalPrice();
        if (invoice.getInvoiceData() != null && !invoice.getInvoiceData().isEmpty()){
            this.invoiceData.putAll(invoice.getInvoiceData());
        }
        this.createdDate = invoice.getCreatedDate();
        this.deliveredDate = invoice.getDeliveredDate();
        this.paymentDate = invoice.getPaymentDate();
        this.dueDate = invoice.getDueDate();
        this.deletedAt = invoice.getDeletedAt();
    }

    private void duplicatePacks(List<DocumentPack> packs) {
        for (DocumentPack pack : packs) {
            this.packs.add(new DocumentPack(pack));
        }
    }
}
