package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QInvoiceRepository {
    Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds);

    Optional<Invoice> getById(Long id, List<Long> companyIds);

    Optional<Invoice> getById(Long id);

    Optional<Invoice> getByIdSimple(Long id, List<Long> companyIds);

    List<Invoice> getAllInvoicesIdInAndType(List<Long> ids, DocumentType type, List<Long> companyIds);

    Invoice getLastInvoice(DocumentType type, Long companyId);
}
