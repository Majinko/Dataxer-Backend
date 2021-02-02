package com.data.dataxer.services;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface InvoiceService {
    void store(Invoice invoice);

    void store(Invoice invoice, Long oldInvoiceId);

    void storeTaxDocument(Invoice taxDocument, Long proformaInvoiceId);

    void storeSummaryInvoice(Invoice summaryInvoice, Long taxDocumentId, Long proformaId);

    void update(Invoice invoice);

    Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    Invoice getById(Long id);

    Invoice getByIdSimple(Long id);

    void destroy(Long id);

    void changeState(Long id, DocumentState documentState, LocalDate payedDate);

    Invoice duplicate(Long id);

    //parameter je id zalohovej faktury
    Invoice generateTaxDocument(Long proformaInvoiceId);

    Invoice generateSummaryInvoice(Long id);

    Invoice changeTypeAndSave(Long id, String type, String number);
}
