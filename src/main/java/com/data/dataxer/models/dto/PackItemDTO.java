package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PackItemDTO {
    private Long id;
    private ItemDTO item;
    private float qty;
}
