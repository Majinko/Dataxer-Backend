package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.DocumentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentRelationDTO {
    private Long relatedDocumentId;
    private String documentTitle;
    private DocumentType documentType;
}
