package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    //@Query("select i from Invoice i left join fetch i.packs where i.company.id = ?2 and i.id in ?1")
    List<Invoice> findAllByIdInAndCompanyId(List<Long> invoiceIds, Long companyId);

    List<Invoice> findAllByDocumentTypeAndIdInAndCompanyIdIn(DocumentType documentType, List<Long> invoiceIds, Long companyId);

    Invoice findByIdAndCompanyId(Long invoiceId, Long companyId);

    List<Invoice> findAllByProjectIdAndCompanyIdIn(Long projectId, List<Long> companyIds);
}
