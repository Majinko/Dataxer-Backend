package com.data.dataxer.models.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@FilterDef(
        name = "companyCondition",
        parameters = @ParamDef(name = "companyId", type = "long")
)
@Filter(
        name = "companyCondition",
        condition = "company_id = :companyId"
)
public class ItemPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @Column(name = "price", nullable = false)
    /*@DecimalMin(value = "0.00", message = "*Price has to be non negative number")*/
    private BigDecimal price;

    @Column(name = "wholesalePrice", nullable = false)
    private BigDecimal wholesalePrice;

    private Integer tax;

    private Integer wholesaleTax;

    private Float marge;
    private Float surcharge;
}
