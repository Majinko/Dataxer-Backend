package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.DocumentRelations;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DocumentRelationsRepository extends CrudRepository<DocumentRelations, Long> {

    @Query(value = "SELECT * FROM document_relations WHERE company_id IN ?2 AND relation_document_id = ?1", nativeQuery = true)
    List<DocumentRelations> findOriginalDocumentIdByRelative(Long id, List<Long> companyIds);

    @Query(value = "SELECT * FROM document_relations WHERE company_id IN ?2 AND document_id = ?1", nativeQuery = true)
    List<DocumentRelations> findAllRelationDocuments(Long originalId, List<Long> companyIds);

}
