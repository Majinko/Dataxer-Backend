package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentDTO {
    private Long documentId;
    DocumentPackDTO documentPack;
    private String documentType;
}
