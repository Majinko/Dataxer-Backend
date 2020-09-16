package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.NewInvoice;
import com.data.dataxer.models.enums.DocumentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewInvoiceService {

    void store(NewInvoice invoice);

    void update(NewInvoice invoice);

    Page<NewInvoice> paginate(Pageable pageable, Filter[] filters);

    NewInvoice getById(Long id);

    NewInvoice getByIdSimple(Long id);

    Page<NewInvoice> getByClient(Pageable pageable, Long contactId);

    void destroy(Long id);

    void changeState(Long id, DocumentState documentState);

    NewInvoice duplicate(Long id);

}
