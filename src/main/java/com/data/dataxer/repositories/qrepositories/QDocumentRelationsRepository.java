package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.DocumentRelations;

import java.util.List;
import java.util.Optional;

public interface QDocumentRelationsRepository {

    Optional<DocumentRelations> getById(Long id, Long companyId);

    List<DocumentRelations> getAllRelatedByOriginalId(Long originalDocumentId, Long companyId);

}
