package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorageFileDTO {
    private Long id;
    private Long size;
    private String contentType;
    private String fileName;
    private String path;
    private Boolean isDefault;

    private byte[] content;
}
