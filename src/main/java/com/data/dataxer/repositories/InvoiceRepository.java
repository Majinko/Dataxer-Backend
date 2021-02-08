package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    List<Invoice> findAllByIdInAndCompanyId(List<Long> invoiceIds, Long companyId);

    List<Invoice> findAllByDocumentTypeAndIdInAndCompanyIdIn(DocumentType documentType, List<Long> invoiceIds, Long companyId);

    Invoice findByIdAndCompanyId(Long invoiceId, Long companyId);
}
