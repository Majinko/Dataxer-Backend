package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.NewInvoice;
import org.springframework.data.repository.CrudRepository;

public interface NewInvoiceRepository extends CrudRepository<NewInvoice, Long> {
}
