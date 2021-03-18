package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface QInvoiceRepository {
    Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId);

    Optional<Invoice> getById(Long id, Long companyId);

    Optional<Invoice> getById(Long id);

    Optional<Invoice> getByIdSimple(Long id, Long companyId);
}
