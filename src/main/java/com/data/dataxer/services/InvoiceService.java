package com.data.dataxer.services;

import com.data.dataxer.models.domain.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {
    void store(Invoice invoice);

    void store(Invoice invoice, Long oldInvoiceId);

    void storeSummaryInvoice(Invoice summaryInvoice, Long taxDocumentId, Long proformaId);

    void update(Invoice invoice);

    Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    Invoice getById(Long id);

    Invoice getByIdSimple(Long id);

    void destroy(Long id);

    void makePay(Long id, LocalDate payedDate);

    Invoice duplicate(Long id);

    //parameter je id zalohovej faktury
    Invoice generateTaxDocument(Long proformaInvoiceId);

    //parameter je id danoveho dokladu
    Invoice generateSummaryInvoice(Long taxDocumentId);

    Invoice changeTypeAndSave(Long id, String type, String number);

    List<Invoice> findAllByRelatedDocuments(Long documentId);
}
