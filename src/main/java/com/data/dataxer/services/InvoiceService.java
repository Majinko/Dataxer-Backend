package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {

    void store(Invoice invoice);

    void update(Invoice invoice);

    Page<Invoice> paginate(Pageable pageable, Filter filter);

    Invoice getById(Long id);

    Invoice getByIdSimple(Long id);

    Page<Invoice> getByClient(Pageable pageable, Long contactId);

    void destroy(Long id);

    void changeState(Long id, DocumentState documentState);

    Invoice duplicate(Long id);

}
