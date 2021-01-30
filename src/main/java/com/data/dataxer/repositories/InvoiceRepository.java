package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Invoice;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
    Optional<Invoice> findByNumberAndCompanyIdIn(String number, List<Long> companyIds);

    List<Invoice> findAllByIdInAndCompanyIdIn(List<Long> invoiceIds, List<Long> companyIds);
}
