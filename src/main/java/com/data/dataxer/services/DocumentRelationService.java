package com.data.dataxer.services;
import com.data.dataxer.models.domain.DocumentBase;

import java.util.List;

public interface DocumentRelationService {
    void store(Long originalDocumentId, Long relatedDocumentId);

    List<DocumentBase> getRelatedDocuments(Long id);

    List<DocumentBase> search(Long documentId, String queryString);

    void destroy(Long documentId, Long relationDocumentId);
}
