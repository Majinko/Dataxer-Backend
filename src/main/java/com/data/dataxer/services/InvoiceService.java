package com.data.dataxer.services;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {

    void store(Invoice invoice);

    Invoice storeTaxDocument(Invoice taxDocument, Long proformaInvoiceId);
    
    Invoice storeSummaryInvoice(Invoice summaryInvoice, Long taxDocumentId, Long proformaId);

    void update(Invoice invoice);

    Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    Invoice getById(Long id);

    Invoice getByIdSimple(Long id);

    void destroy(Long id);

    void changeState(Long id, DocumentState documentState);

    Invoice duplicate(Long id);

    Invoice generateTaxDocument(Long id);

    Invoice generateSummaryInvoice(Long id);
}
