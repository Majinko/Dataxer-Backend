package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@DiscriminatorValue("INVOICE")
public class NewInvoice extends DocumentBase {

    private LocalDate paymentDate;

}
