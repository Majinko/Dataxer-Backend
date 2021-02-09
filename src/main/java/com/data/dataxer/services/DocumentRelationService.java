package com.data.dataxer.services;
import com.data.dataxer.models.domain.DocumentBase;
import com.data.dataxer.models.domain.DocumentRelation;

import java.util.List;

public interface DocumentRelationService {
    DocumentRelation store(Long originalDocumentId, Long relatedDocumentId);

    List<DocumentBase> getRelatedDocuments(Long id);

    List<DocumentBase> search(String queryString);

    void destroy(Long documentId, Long relationDocumentId);
}
