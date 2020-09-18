package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DeliveryMethod;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Setter
@Getter
@DiscriminatorValue("INVOICE")
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE document_base SET deleted_at = now() WHERE id = ?")
public class Invoice extends DocumentBase {

    private String variableSymbol;

    private String specificSymbol;

    private DeliveryMethod deliveryMethod;

    private PaymentMethod paymentMethod;

    @Column(columnDefinition = "text")
    private String headerComment;

    private LocalDate paymentDate;

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
        if (invoice.getDocumentData() != null && !invoice.getDocumentData().isEmpty()){
            this.documentData.putAll(invoice.getDocumentData());
        }
        this.createdDate = invoice.getCreatedDate();
        this.deliveredDate = invoice.getDeliveredDate();
        this.dueDate = invoice.getDueDate();
        this.deletedAt = invoice.getDeletedAt();
        this.variableSymbol = invoice.getVariableSymbol();
        this.specificSymbol = invoice.getSpecificSymbol();
        this.deliveryMethod = invoice.getDeliveryMethod();
        this.paymentMethod = invoice.getPaymentMethod();
        this.headerComment = invoice.getHeaderComment();
        this.paymentDate = invoice.getPaymentDate();
    }

    private void duplicatePacks(List<DocumentPack> packs) {
        for (DocumentPack pack : packs) {
            this.packs.add(new DocumentPack(pack));
        }
    }
}
