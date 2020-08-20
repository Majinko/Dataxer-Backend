package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Invoice;
import org.springframework.data.repository.CrudRepository;

public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
}
