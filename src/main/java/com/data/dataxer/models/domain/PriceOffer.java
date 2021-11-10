package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Setter
@Getter
@DiscriminatorValue("PRICE_OFFER")
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE document_base SET deleted_at = now() WHERE id = ?")
public class PriceOffer extends DocumentBase {

    @Override
    public BigDecimal countDiscountTotalPrice() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal countTaxPrice(BigDecimal price, Integer tax) {
        return BigDecimal.ZERO;
    }
}
