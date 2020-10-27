package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DeliveryMethod;
import com.data.dataxer.models.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@Setter
@Getter
@DiscriminatorValue("INVOICE")
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE document_base SET deleted_at = now() WHERE id = ?")
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

    public BigDecimal countDiscountTotalPrice() {
        System.out.println("TP:" + totalPrice + " D: " + discount);
        return this.totalPrice.multiply(this.discount.divide(BigDecimal.valueOf(100)));
    }

    public Map<Integer, BigDecimal> getTaxesAndValues() {
        Map<Integer, BigDecimal> taxesAndValues = new HashMap<>();

        for (DocumentPack pack: this.packs) {
            Map<Integer, BigDecimal> packTaxesAndValues = pack.getPackItemsTaxesAndValues();
            for (Integer key : packTaxesAndValues.keySet()) {
                if (taxesAndValues.containsKey(key)) {
                    BigDecimal value = taxesAndValues.get(key);
                    value = value.add(packTaxesAndValues.get(key));
                    taxesAndValues.replace(key, value);
                } else {
                    taxesAndValues.put(key, packTaxesAndValues.get(key));
                }
            }
        }

        return  taxesAndValues;
    }

    public BigDecimal countTaxPrice(BigDecimal price, Integer tax) {
        return price.multiply(new BigDecimal(tax.floatValue()/100));
    }
}
