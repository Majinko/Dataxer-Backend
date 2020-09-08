package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class FileDTO {

    private String name;
    private String extension;
    private BigDecimal size;
    private String downloadURL;
    private String showURL;
    private Boolean isDefault;

}
