package com.data.dataxer.services;

import com.data.dataxer.models.domain.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {

    void store(Invoice invoice);

    void update(Invoice invoice);

    Page<Invoice> paginate(Pageable pageable, String filter);

    Invoice getById(Long id);

    Invoice getByIdSimple(Long id);

    Page<Invoice> getByClient(Pageable pageable, Long contactId);

    void destroy(Long id);

    void changeState(Invoice invoice);
}
