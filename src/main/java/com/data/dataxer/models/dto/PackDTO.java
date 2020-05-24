package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PackDTO {
    private Long id;
    private String title;
    private List<PackItemDTO> items;
}
