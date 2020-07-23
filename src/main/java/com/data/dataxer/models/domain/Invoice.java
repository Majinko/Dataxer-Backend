package com.data.dataxer.models.domain;

import com.data.dataxer.mappers.HashMapConverter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Setter
@Getter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE price_offer SET deleted_at = now() WHERE id = ?")
public class Invoice extends  BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @OneToMany(mappedBy = "documentPackId", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    List<DocumentPack> packs = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    Contact contact;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String number;

    private String state;

    @Column(columnDefinition = "text")
    private String note;

    private BigDecimal priceWithoutTax;

    @Column(columnDefinition = "text")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> invoiceData;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Column(nullable = false)
    private LocalDate deliveryDate;

    //represent date when invoice was changed to payed state
    private LocalDate paymentDate;

    //represent date when invoice should be payed
    @Column(nullable = false)
    private LocalDate dueDate;

    private LocalDateTime deletedAt;

}
