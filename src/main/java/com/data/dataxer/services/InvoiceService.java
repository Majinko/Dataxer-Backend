package com.data.dataxer.services;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {
    void store(Invoice invoice);

    void store(Invoice invoice, Long oldInvoiceId);

    Invoice storeTaxDocument(Invoice taxDocument, Long proformaInvoiceId);

    Invoice storeSummaryInvoice(Invoice summaryInvoice, Long taxDocumentId, Long proformaId);

    void update(Invoice invoice);

    Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    Invoice getById(Long id);

    Invoice getByIdSimple(Long id);

    void destroy(Long id);

    void makePay(Long id, LocalDate payedDate);

    Invoice duplicate(Long id);

    Invoice generateTaxDocument(Long id);

    Invoice generateSummaryInvoice(Long id);

    Invoice changeTypeAndSave(Long id, String type, String number);

    List<Invoice> findAllByRelatedDocuments(Long documentId);
}
