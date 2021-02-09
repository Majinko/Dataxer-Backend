package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.DocumentRelation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRelationsRepository extends CrudRepository<DocumentRelation, Long> {
    // todo native query replace to jpa query

    @Query(value = "SELECT * FROM document_relation WHERE company_id = ?2 AND relation_document_id = ?1", nativeQuery = true)
    List<DocumentRelation> findOriginalDocumentIdByRelative(Long id, Long companyId);

    @Query("Select d FROM DocumentRelation d where d.documentId = ?1 and d.company.id = ?2")
    List<DocumentRelation> findAllRelationDocuments(Long originalId, Long companyId);

    List<DocumentRelation> findAllByDocumentIdAndCompanyId(Long documentId, Long CompanyId);

    Optional<DocumentRelation> findByDocumentIdAndRelationDocumentIdAndCompanyId(Long documentId, Long relatedDocumentId, Long companyId);
}
