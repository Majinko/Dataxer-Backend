package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    @Query(value = "SELECT * FROM payment WHERE app_profile_id = ?1 AND document_id = ?2 AND tax_document_created = false", nativeQuery = true)
    List<Payment> findAllWithoutTaxDocumentByDocumentId(Long defaultProfile, Long id);

    List<Payment> findAllByDocumentIdAndDocumentType(Long documentId, DocumentType documentType);
}
