package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemandPackItemDTO {
    private Long id;
    private String title;
    private String unit;
    private Float qty;
    private int position;

    private DemandDTO demand;
    private DemandPackDTO demandPack;
    private CategoryDTO category;
}
