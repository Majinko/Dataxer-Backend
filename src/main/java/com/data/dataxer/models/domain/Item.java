package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.ItemPriceLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Item extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    // represent code of item on the other web
    String code;

    String title;

    // model of items, v1, v2...
    String model;

    String manufacturer;

    ItemPriceLevel itemPriceLevel;

    String timeDelivery;

    @Column(columnDefinition = "text")
    String text;

    @Column(name = "price", nullable = false)
    @DecimalMin(value = "0.00", message = "*Price has to be non negative number")
    private BigDecimal price;

    @Column(name = "wholesalePrice", nullable = false)
    @DecimalMin(value = "0.00")
    private BigDecimal wholesalePrice;

    private Integer tax;

    private Integer wholesaleTax;

    @ManyToMany
    private List<Category> categories = new ArrayList<Category>();

    private LocalDateTime deletedAt;
}
