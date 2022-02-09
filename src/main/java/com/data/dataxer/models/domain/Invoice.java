package com.data.dataxer.models.domain;

import com.data.dataxer.Listeners.InvoiceListener;
import com.data.dataxer.models.enums.DeliveryMethod;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@DiscriminatorValue("INVOICE")
@SQLDelete(sql = "UPDATE document_base SET deleted_at = now() WHERE id = ?")
@EntityListeners(InvoiceListener.class)
public class Invoice extends DocumentBase {
    private String variableSymbol;

    private String specificSymbol;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(columnDefinition = "text")
    private String headerComment;

    private LocalDate paymentDate;

    @JoinTable(
            name = "payment",
            joinColumns = @JoinColumn(name = "documentId"),
            inverseJoinColumns = @JoinColumn(name = "id"),
            foreignKey = @javax.persistence.ForeignKey(name = "none")
    )
    @OneToMany(fetch = FetchType.LAZY)
    @Where(clause="document_type!='COST'")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<Payment> payments = new ArrayList<>();

    @Override
    public BigDecimal countDiscountTotalPrice() {
        return this.totalPrice.multiply(this.discount.divide(BigDecimal.valueOf(100)));
    }

    /**
     * Vrati sumu s DPH na zaklade ceny bez DPH a dane
     * @param price
     * @param tax
     * @return
     */
    public BigDecimal countTaxPrice(BigDecimal price, Integer tax) {
        return price.multiply(new BigDecimal (tax.doubleValue()/100)).setScale(2, RoundingMode.HALF_UP);
    }
}
