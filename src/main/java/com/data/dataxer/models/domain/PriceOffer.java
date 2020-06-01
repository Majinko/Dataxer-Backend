package com.data.dataxer.models.domain;

import com.data.dataxer.mappers.HashMapConverter;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Setter
@Getter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE price_offer SET deleted_at = now() WHERE id = ?")
public class PriceOffer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(columnDefinition = "text")
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> priceOfferData;

    @Column(nullable = false)
    private LocalDate createdDate;

    @Column(nullable = false)
    private LocalDate deliveredDate;

    private LocalDate dueDate;

    private LocalDateTime deletedAt;
}
