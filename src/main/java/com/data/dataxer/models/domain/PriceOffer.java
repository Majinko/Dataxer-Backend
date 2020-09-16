package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@DiscriminatorValue("PRICE_OFFER")
public class PriceOffer extends DocumentBase {

}
