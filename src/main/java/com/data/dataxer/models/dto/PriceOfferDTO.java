package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

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

    private ContactDTO contact;
}
