package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QInvoiceRepository {
    Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);

    Optional<Invoice> getById(Long id, Long appProfileId);

    Optional<Invoice> getById(Long id);

    Optional<Invoice> getByIdSimple(Long id, Long appProfileId);

    List<Invoice> getAllInvoicesIdInAndType(List<Long> ids, DocumentType type, Long appProfileId);

    Invoice getLastInvoice(DocumentType type, Long companyId, Long appProfileId);

    Invoice getLastInvoice(Long companyId, Long appProfileId);

    Invoice getLastInvoiceByMonthAndYear(LocalDate localDate, Long companyId, Long appProfileId);
}
