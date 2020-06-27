package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
public class PriceOfferDTO {
    private Long id;
    private String title;
    private String number;
    private String state;
    private String note;
    private Map<String, Object> priceOfferData;
    private LocalDate createdDate;
    private LocalDate deliveredDate;
    private LocalDate dueDate;
    private BigDecimal price;
    private BigDecimal totalPrice;

    private ContactDTO contact;
    List<PriceOfferPackDTO> packs = new ArrayList<>();
}
