package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.DocumentRelations;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DocumentRelationsRepository extends CrudRepository<DocumentRelations, Long> {
    // todo native query replace to jpa query

    @Query(value = "SELECT * FROM document_relations WHERE company_id = ?2 AND relation_document_id = ?1", nativeQuery = true)
    List<DocumentRelations> findOriginalDocumentIdByRelative(Long id, Long companyId);

    @Query(value = "SELECT * FROM document_relations WHERE document_id = ?1", nativeQuery = true)
    List<DocumentRelations> findAllRelationDocuments(Long originalId);

    List<DocumentRelations> findAllByDocumentIdAndCompanyId(Long documentId, Long CompanyId);
}
