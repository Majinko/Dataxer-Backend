package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    @Query("select i from Invoice i left join fetch i.payments p where p.documentType in ('INVOICE', 'TAX_DOCUMENT', 'SUMMARY_INVOICE', 'PROFORMA')")
    List<Invoice> findAllWithPayments();

    @Query("select i from Invoice  i left join fetch i.company where i.id in ?1 and i.appProfile.id = ?2")
    List<Invoice> findAllByIdInAndAppProfileId(List<Long> invoiceIds, Long appProfileId);

    List<Invoice> findAllByDocumentTypeAndIdInAndAppProfileId(DocumentType documentType, List<Long> invoiceIds, Long appProfileId);

    Invoice findByIdAndAppProfileId(Long invoiceId, Long appProfileId);

    List<Invoice> findAllByProjectIdAndAppProfileId(Long projectId, Long appProfileId);
}
