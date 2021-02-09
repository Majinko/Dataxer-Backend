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

    @Query("Select d FROM DocumentRelations d where d.documentId = ?1 and d.company.id = ?2")
    List<DocumentRelations> findAllRelationDocuments(Long originalId, Long companyId);

    List<DocumentRelations> findAllByDocumentIdAndCompanyId(Long documentId, Long CompanyId);

    DocumentRelations findAllByDocumentIdAndAndRelationDocumentIdAAndCompanyId(Long documentId, Long relatedDocumentId, Long companyId);
}
