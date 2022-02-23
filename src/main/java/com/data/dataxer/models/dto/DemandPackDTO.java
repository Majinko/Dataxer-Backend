package com.data.dataxer.models.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DemandPackDTO {
    private Long id;
    private String title;

    private boolean showItems;

    private DemandDTO demand;
    private List<DemandPackItemDTO> packItems;
}
