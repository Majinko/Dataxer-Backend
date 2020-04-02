package com.data.dataxer.models.domain;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Entity
public class ItemPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Column(name = "price", nullable = false)
    @DecimalMin(value = "0.00", message = "*Price has to be non negative number")
    private BigDecimal price;

    @Column(name = "wholesalePrice", nullable = false)
    @DecimalMin(value = "0.00")
    private BigDecimal wholesalePrice;

    private Integer tax;

    private Integer wholesaleTax;

    private Float marge;
    private Float surcharge;
}
