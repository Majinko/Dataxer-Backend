package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorageFileDTO {
    private Long size;
    private String contentType;
    private String fileName;
    private Boolean isDefault;

    private byte[] content;
}
