package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentRelations;

import java.util.List;

public interface DocumentRelationService {

    void store(DocumentRelations documentRelations);

    Long getOriginalDocumentId(Long relativeDocumentId);

    List<Long> getAllRelationDocuments(Long originalDocumentId);
}
