package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DeliveryMethod;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@DiscriminatorValue("INVOICE")
@SQLDelete(sql = "UPDATE document_base SET deleted_at = now() WHERE id = ?")
public class Invoice extends DocumentBase {
    private String variableSymbol;

    private String subject;

    private String specificSymbol;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(columnDefinition = "text")
    private String headerComment;

    private LocalDate paymentDate;

    @Override
    public BigDecimal countDiscountTotalPrice() {
        return this.totalPrice.multiply(this.discount.divide(BigDecimal.valueOf(100)));
    }

    public BigDecimal countTaxPrice(BigDecimal price, Integer tax) {
        return price.multiply(new BigDecimal(tax.floatValue() / 100));
    }
}
