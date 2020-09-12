package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@DiscriminatorValue("PRICE_OFFER")
public class NewPriceOffer extends DocumentBase {

    private BigDecimal discount;



}
