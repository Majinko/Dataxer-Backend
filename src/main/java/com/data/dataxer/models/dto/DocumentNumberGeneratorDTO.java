package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.Periods;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentNumberGeneratorDTO {
    private Long id;
    private String title;
    private String format;
    private DocumentType type;
    private Periods period;
    private Integer isDefault;
}
