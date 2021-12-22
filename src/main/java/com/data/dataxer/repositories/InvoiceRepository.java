package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    List<Invoice> findAllByIdInAndCompanyId(List<Long> invoiceIds, Long companyId);

    List<Invoice> findAllByDocumentTypeAndIdInAndCompanyIdIn(DocumentType documentType, List<Long> invoiceIds, Long companyId);

    Invoice findByIdAndCompanyIdIn(Long invoiceId, List<Long> companyIds);

    List<Invoice> findAllByProjectIdAndCompanyIdIn(Long projectId, List<Long> companyIds);
}
