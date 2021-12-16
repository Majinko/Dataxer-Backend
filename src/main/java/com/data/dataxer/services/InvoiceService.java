package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentPack;
import com.data.dataxer.models.domain.DocumentPackItem;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public interface InvoiceService {
    void store(Invoice invoice);

    void store(Invoice invoice, Long oldInvoiceId);

    void storeSummaryInvoice(Invoice summaryInvoice, Long taxDocumentId, Long proformaId);

    void update(Invoice invoice);

    Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    Invoice getById(Long id);

    Invoice getByIdWithoutFirm(Long id);

    Invoice getByIdSimple(Long id);

    void destroy(Long id);

    void makePay(Long id, LocalDate payedDate);

    Invoice duplicate(Long id);

    //parameter je id zalohovej faktury
    Invoice generateTaxDocument(Long proformaInvoiceId);

    HashMap<Integer, BigDecimal> getTaxesValuesMap(List<DocumentPackItem> documentPackItems);

    HashMap<Integer, BigDecimal> getPayedTaxesValuesMap(List<DocumentPack> packs);

    HashMap<Integer, BigDecimal> getTaxPayedTaxesValuesMap(List<DocumentPack> packs);

    List<DocumentPackItem> getInvoiceItems(List<DocumentPack> packs);
    //parameter je id danoveho dokladu

    Invoice generateSummaryInvoice(Long taxDocumentId);

    Invoice changeTypeAndSave(Long id, String type, String number);

    List<Invoice> findAllByRelatedDocuments(Long documentId);

    List<Invoice> findAllByProject(Long projectId, List<Long> companyIds);

    Invoice generateFromPriceOfferByType(Long id, DocumentType type);
}
