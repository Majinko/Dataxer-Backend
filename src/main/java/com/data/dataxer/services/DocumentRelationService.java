package com.data.dataxer.services;
import com.data.dataxer.models.dto.DocumentRelationDTO;

import java.util.List;

public interface DocumentRelationService {
    void store(Long originalDocumentId, Long relatedDocumentId);

    List<DocumentRelationDTO> getRelatedDocuments(Long id);

    void destroy(Long documentId, Long relationDocumentId);
}
