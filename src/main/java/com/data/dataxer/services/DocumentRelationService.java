package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentRelations;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentType;

import java.util.List;

public interface DocumentRelationService {

    void store(Long originalDocumentId, Long relatedDocumentId);

    void storeDocumentRelation(DocumentRelations documentRelations);

    DocumentRelations getById(Long id);

    Long getOriginalDocumentId(Long relativeDocumentId);

    List<Long> getAllRelationDocumentIds(Long originalDocumentId);

    List<Invoice> getAllRelatedDocuments(Long originalDocumentId);

    List<Invoice> getAllRelationDocumentsByDocumentType(Long originalDocumentId, DocumentType documentType);

}
