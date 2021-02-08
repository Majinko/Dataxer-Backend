package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.DocumentRelationsRepository;
import com.data.dataxer.repositories.InvoiceRepository;
import com.data.dataxer.repositories.qrepositories.QInvoiceRepository;
import com.data.dataxer.repositories.qrepositories.QPaymentRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final QInvoiceRepository qInvoiceRepository;
    private final QPaymentRepository qPaymentRepository;
    private final DocumentRelationsRepository documentRelationsRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, QInvoiceRepository qInvoiceRepository, QPaymentRepository qPaymentRepository, DocumentRelationsRepository documentRelationsRepository) {
        this.invoiceRepository = invoiceRepository;
        this.qInvoiceRepository = qInvoiceRepository;
        this.qPaymentRepository = qPaymentRepository;
        this.documentRelationsRepository = documentRelationsRepository;
    }

    @Override
    @Transactional
    public void store(Invoice invoice) {
        Invoice i = this.invoiceRepository.save(invoice);

        this.setInvoicePackAndItems(i);
    }

    @Override
    @Transactional
    public void store(Invoice invoice, Long oldInvoiceId) { // store invoice from old invoice
        invoice.getPacks().forEach(documentPack -> {
            documentPack.setId(null);

            documentPack.getPackItems().forEach(documentPackItem -> {
                documentPackItem.setId(null);
            });
        });

        if (invoice.getDocumentType().equals(DocumentType.TAX_DOCUMENT)) {
            invoice.setPaymentDate(LocalDate.now());
        }

        this.store(invoice);
        this.storeRelation(oldInvoiceId, invoice.getId());
    }


    @Override
    @Transactional
    public void storeSummaryInvoice(Invoice summaryInvoice, Long taxDocumentId, Long proformaId) {
        Invoice i = this.invoiceRepository.save(summaryInvoice);

        this.setInvoicePackAndItems(i);
        this.storeAllSummaryInvoiceRelations(taxDocumentId, summaryInvoice.getId(), proformaId);
    }

    @Override
    public void update(Invoice invoice) {
        Invoice invoiceUpdated = this.setInvoicePackAndItems(invoice);

        this.invoiceRepository.save(invoiceUpdated);
    }

    @Override
    public Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter) {
        return this.qInvoiceRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.CompanyId(), disableFilter);
    }

    @Override
    public Invoice getById(Long id, Boolean disableFilter) {
        return this.qInvoiceRepository
                .getById(id, SecurityUtils.CompanyId(), disableFilter)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public Invoice getByIdWithoutFirm(Long id) {
        return this.qInvoiceRepository
                .getById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public Invoice getByIdSimple(Long id, Boolean disableFilter) {
        return this.qInvoiceRepository
                .getByIdSimple(id, SecurityUtils.CompanyId(), disableFilter)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public void destroy(Long id) {
        this.invoiceRepository.delete(this.getByIdSimple(id, false));
    }

    @Override
    public void makePay(Long id, LocalDate payedDate) {
        Invoice invoice = this.qInvoiceRepository.getByIdSimple(id, SecurityUtils.CompanyId(), false).orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setPaymentDate(payedDate);
        this.invoiceRepository.save(invoice);
    }

    @Override
    @Transactional
    public Invoice changeTypeAndSave(Long id, String type, String number) {
        Invoice originalInvoice = this.getById(id, false);
        Invoice invoice = new Invoice();

        BeanUtils.copyProperties(originalInvoice, invoice, "id", "packs");

        invoice.setNumber(number);
        invoice.setVariableSymbol(number);
        invoice.setTitle(this.generateInvoiceTitle(type, number));
        invoice.setDeliveredDate(LocalDate.now());
        invoice.setCreatedDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(14));
        invoice.setDocumentType(DocumentType.valueOf(type.toUpperCase()));

        this.invoiceRepository.save(invoice);

        return invoice;
    }

    @Override
    public List<Invoice> findAllByRelatedDocuments(Long documentId) {
        return this.invoiceRepository.findAllByIdInAndCompanyIdIn(
                documentRelationsRepository.findAllByDocumentIdAndCompanyIdIn(documentId, SecurityUtils.companyIds()).stream().map(DocumentRelations::getRelationDocumentId).collect(Collectors.toList()),
                SecurityUtils.companyIds()
        );
    }

    @Override
    @Transactional
    public Invoice duplicate(Long id) {
        Invoice originalInvoice = this.getById(id, false);
        Invoice duplicatedInvoice = new Invoice();
        BeanUtils.copyProperties(originalInvoice, duplicatedInvoice, "id", "packs");
        duplicatedInvoice.setPacks(this.duplicateDocumentPacks(originalInvoice.getPacks()));
        this.setInvoicePackAndItems(duplicatedInvoice);
        this.invoiceRepository.save(duplicatedInvoice);
        return duplicatedInvoice;
    }

    @Override
    public Invoice generateTaxDocument(Long proformaInvoiceId) {
        Invoice proformaInvoice = this.getById(proformaInvoiceId, false);

        Invoice taxDocument = new Invoice();
        BeanUtils.copyProperties(proformaInvoice, taxDocument,
                "id", "packs", "title", "note", "number", "state", "discount", "price",
                "totalPrice", "documentData", "createdDate", "variableSymbol", "headerComment",
                "paymentMethod", "invoiceType");
        taxDocument.setCreatedDate(LocalDate.now());
        taxDocument.setDocumentType(DocumentType.TAX_DOCUMENT);
        taxDocument.setPacks(this.setTaxDocumentsPacks(proformaInvoice));

        return taxDocument;
    }

    @Override
    public Invoice generateSummaryInvoice(Long taxDocumentId) {
        Invoice proformaInvoice = this.getOriginalProformaInvoiceFromTaxDocument(taxDocumentId);
        Invoice taxDocument = this.getById(taxDocumentId, false);

        Invoice summaryInvoice = new Invoice();
        BeanUtils.copyProperties(taxDocument, summaryInvoice,
                "id", "packs", "title", "note", "number", "state", "discount", "price",
                "totalPrice", "documentData", "createdDate", "variableSymbol", "headerComment",
                "paymentMethod", "invoiceType");
        summaryInvoice.setDocumentType(DocumentType.SUMMARY_INVOICE);

        List<DocumentPack> summaryInvoicePacks = new ArrayList<>(proformaInvoice.getPacks());

        for (DocumentPack documentPack:taxDocument.getPacks()) {
            summaryInvoicePacks.add(this.generateDocumentPackForSummaryInvoice(documentPack, taxDocument.getNumber(),
                    taxDocument.getCreatedDate(), taxDocument.getVariableSymbol()));
        }

        summaryInvoice.setPacks(summaryInvoicePacks);

        return summaryInvoice;
    }

    private Invoice getOriginalProformaInvoiceFromTaxDocument(Long taxDocumentId) {
        List<Invoice> invoices = this.invoiceRepository.findAllByIdInAndCompanyIdIn(
                this.documentRelationsRepository.findOriginalDocumentIdByRelative(taxDocumentId, SecurityUtils.companyIds())
                        .stream().map(DocumentRelations::getDocumentId).collect(Collectors.toList()),
                SecurityUtils.companyIds()
        );
        for (Invoice invoice:invoices) {
            if (invoice.getDocumentType().equals(DocumentType.PROFORMA)) {
                return invoice;
            }
        }
        throw new RuntimeException("Proforma invoice not found to create summary invoice");
    }

    private List<DocumentPack> setTaxDocumentsPacks(Invoice proformaInvoice) {
        List<DocumentPack> documentPacks = new ArrayList<>();

        //priprava potrebnych dat**********
        BigDecimal payed = this.getPayedValueFromRelatedType(proformaInvoice.getId(), DocumentType.TAX_DOCUMENT);
        BigDecimal payments = this.getPaymentsValue(proformaInvoice.getId());

        //storing keys desc****************
        HashMap<Integer, BigDecimal> valuesForTaxes = getTaxesValuesMap(this.getInvoiceItems(proformaInvoice.getPacks()));
        ArrayList<Integer> sortedKeys = new ArrayList<>(valuesForTaxes.keySet());
        Collections.sort(sortedKeys);
        Collections.reverse(sortedKeys);
        //*********************************

        for (Integer keyTax : sortedKeys) {
            if (payments.compareTo(BigDecimal.ZERO) > 0 && valuesForTaxes.get(keyTax).compareTo(payed) > 0) {
                BigDecimal totalPrice = payments.compareTo(valuesForTaxes.get(keyTax)) > 0 ? valuesForTaxes.get(keyTax) : payments;
                if (payed.compareTo(BigDecimal.ZERO) > 0) {
                    totalPrice = totalPrice.subtract(payed);
                }

                documentPacks.add(generateDocumentPackForTaxDocument(proformaInvoice, keyTax, totalPrice));
            }

            payed = payed.subtract(valuesForTaxes.get(keyTax));
            payments = payments.subtract(valuesForTaxes.get(keyTax));
        }

        return documentPacks;
    }

    private DocumentPack generateDocumentPackForTaxDocument(Invoice proformaInvoice, Integer tax, BigDecimal totalPrice) {
        DocumentPack taxDocumentPack = new DocumentPack();

        taxDocumentPack.setTax(tax);
        taxDocumentPack.setPrice(getPriceFromTotalPrice(totalPrice, tax));
        taxDocumentPack.setTotalPrice(totalPrice);
        taxDocumentPack.setTitle("Daňový doklad k prijatej platbe");

        taxDocumentPack.setPackItems(List.of(generateDocumentPackItemForTaxDocument(proformaInvoice, tax, totalPrice)));

        return taxDocumentPack;
    }

    private DocumentPack generateDocumentPackForSummaryInvoice(DocumentPack taxDocumentPack, String taxDocumentNumber,
                                                               LocalDate taxDocumentCreated, String taxDocumentVariableSymbol) {
        DocumentPack summaryInvoicePack = new DocumentPack();

        summaryInvoicePack.setTitle("Uhradené zálohou");
        summaryInvoicePack.setTax(taxDocumentPack.getTax());
        summaryInvoicePack.setPrice(taxDocumentPack.getPrice().negate());
        summaryInvoicePack.setTotalPrice(taxDocumentPack.getTotalPrice().negate());

        summaryInvoicePack.setPackItems(List.of(generateDocumentPackItemForSummaryInvoice(taxDocumentPack, taxDocumentNumber,
                taxDocumentCreated, taxDocumentVariableSymbol)));

        return summaryInvoicePack;
    }

    private DocumentPackItem generateDocumentPackItemForTaxDocument(Invoice proformaInvoice, Integer tax, BigDecimal totalPrice) {
        DocumentPackItem documentPackItem = new DocumentPackItem();

        documentPackItem.setTitle(this.generateTaxDocumentPackTitle(getNewestPaymentPayedDate(proformaInvoice.getId()), proformaInvoice));
        documentPackItem.setQty(1f);
        documentPackItem.setDiscount(BigDecimal.valueOf(0));
        documentPackItem.setTax(tax);
        documentPackItem.setPrice(getPriceFromTotalPrice(totalPrice, tax));
        documentPackItem.setTotalPrice(totalPrice);

        return documentPackItem;
    }

    private DocumentPackItem generateDocumentPackItemForSummaryInvoice(DocumentPack taxDocumentPack, String taxDocumentNumber,
                                                                       LocalDate taxDocumentCreated, String taxDocumentVariableSymbol) {
        DocumentPackItem documentPackItem = new DocumentPackItem();

        documentPackItem.setTitle(this.generateSummaryInvoicePackTitle(taxDocumentNumber, taxDocumentCreated, taxDocumentVariableSymbol));
        documentPackItem.setTax(taxDocumentPack.getTax());
        documentPackItem.setPrice(taxDocumentPack.getPrice().negate());
        documentPackItem.setTotalPrice(taxDocumentPack.getTotalPrice().negate());

        return documentPackItem;
    }

    // return all item in invoices
    @Override
    public List<DocumentPackItem> getInvoiceItems(List<DocumentPack> packs) {
        List<DocumentPackItem> documentPackItems = new ArrayList<>();

        packs.forEach(pack -> {
            documentPackItems.addAll(pack.getPackItems());
        });

        return documentPackItems;
    }

    @Override
    public HashMap<Integer, BigDecimal> getTaxesValuesMap(List<DocumentPackItem> documentPackItems) {
        HashMap<Integer, BigDecimal> mappedTaxedValues = new HashMap<>();
        for (DocumentPackItem documentPackItem : documentPackItems) {
            if (mappedTaxedValues.containsKey(documentPackItem.getTax())) {
                BigDecimal newValue = mappedTaxedValues.get(documentPackItem.getTax()).add(documentPackItem.getTotalPrice());
                mappedTaxedValues.replace(documentPackItem.getTax(), newValue);
            } else {
                mappedTaxedValues.put(documentPackItem.getTax(), documentPackItem.getTotalPrice());
            }
        }
        return mappedTaxedValues;
    }

    private BigDecimal getPaymentsValue(Long proformaInvoiceId) {
        BigDecimal paymentsValue = BigDecimal.ZERO;
        List<Payment> payments = this.qPaymentRepository.getPaymentsByDocumentIdSortedByPayDate(proformaInvoiceId, SecurityUtils.CompanyId(), false);

        for (Payment payment : payments) {
            paymentsValue = paymentsValue.add(payment.getPayedValue());
        }
        return paymentsValue;
    }

    private BigDecimal getPayedValueFromRelatedType(Long proformaInvoiceId, DocumentType documentType) {
        BigDecimal payedValue = BigDecimal.ZERO;
        List<Invoice> relatedInvoices = this.invoiceRepository.findAllByDocumentTypeAndIdInAndCompanyIdIn(
                documentType,
                documentRelationsRepository.findAllByDocumentIdAndCompanyIdIn(proformaInvoiceId, SecurityUtils.companyIds()).stream().map(DocumentRelations::getRelationDocumentId).collect(Collectors.toList()),
                SecurityUtils.companyIds()
        );

        for (Invoice invoice : relatedInvoices) {
            payedValue = payedValue.add(invoice.getTotalPrice());
        }
        return payedValue;
    }

    private LocalDate getNewestPaymentPayedDate(Long proformaInvoiceId) {
        Payment payment = this.qPaymentRepository.getNewestByDocumentId(proformaInvoiceId, SecurityUtils.CompanyId(), false)
                .orElseThrow(() -> new RuntimeException("No payment usable for tax document"));
        return payment.getPayedDate();
    }

    private String generateTaxDocumentPackTitle(LocalDate date, Invoice proformaInvoice) {
        return "Na základe zálohovej faktúry " + proformaInvoice.getTitle() + " uhradenej " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " VS " + proformaInvoice.getVariableSymbol();
    }

    private String generateSummaryInvoicePackTitle(String taxDocumentNumber, LocalDate taxDocumentCreatedDate, String taxDocumentVariableSymbol) {
        return "Daňový doklad k prijatej platbe " +  taxDocumentNumber + ", zo dňa "
                +  taxDocumentCreatedDate + ", variabilný symbol " + taxDocumentVariableSymbol;
    }

    private Invoice setInvoicePackAndItems(Invoice invoice) {
        int packPosition = 0;

        for (DocumentPack documentPack : invoice.getPacks()) {
            documentPack.setDocument(invoice);
            documentPack.setType(DocumentType.INVOICE);
            documentPack.setPosition(packPosition);
            packPosition++;

            int packItemPosition = 0;

            for (DocumentPackItem packItem : documentPack.getPackItems()) {
                packItem.setPack(documentPack);
                packItem.setPosition(packItemPosition);

                packItemPosition++;
            }
        }

        return invoice;
    }

    private List<DocumentPack> duplicateDocumentPacks(List<DocumentPack> originalDocumentPacks) {
        List<DocumentPack> duplicatedDocumentPacks = new ArrayList<>();
        for (DocumentPack originalDocumentPack : originalDocumentPacks) {
            DocumentPack duplicatePack = new DocumentPack();
            duplicatePack.setPackItems(this.duplicateDocumentPackItems(originalDocumentPack.getPackItems()));
            duplicatedDocumentPacks.add(duplicatePack);
        }
        return duplicatedDocumentPacks;
    }

    private List<DocumentPackItem> duplicateDocumentPackItems(List<DocumentPackItem> originalPackItems) {
        List<DocumentPackItem> duplicatedDocumentPackItems = new ArrayList<>();
        for (DocumentPackItem originalDocumentPackItem : originalPackItems) {
            DocumentPackItem duplicatedDocumentPackItem = new DocumentPackItem();
            BeanUtils.copyProperties(originalDocumentPackItem, duplicatedDocumentPackItem, "id", "pack");
            duplicatedDocumentPackItems.add(duplicatedDocumentPackItem);
        }
        return duplicatedDocumentPackItems;
    }

    private BigDecimal getPriceFromTotalPrice(BigDecimal totalPrice, Integer tax) {
        double taxCoefficient = 1.0 + (tax / 100.0);
        return totalPrice.divide(BigDecimal.valueOf(taxCoefficient), 2, RoundingMode.HALF_UP);
    }

    private void storeAllSummaryInvoiceRelations(Long taxDocumentId, Long summaryInvoiceId, Long proformaId) {
        this.storeRelation(taxDocumentId, summaryInvoiceId);
        this.storeRelation(summaryInvoiceId, taxDocumentId);

        this.storeRelation(summaryInvoiceId, proformaId);
    }

    private void storeRelation(Long documentId, Long relationDocumentId) {
        DocumentRelations documentRelation = new DocumentRelations();
        documentRelation.setDocumentId(documentId);
        documentRelation.setRelationDocumentId(relationDocumentId);

        documentRelationsRepository.save(documentRelation);
    }


    private String generateInvoiceTitle(String type, String number) {
        String title;

        switch (type) {
            case "PROFORMA":
                title = "Zálohová faktúra " + number;
                break;
            default:
                title = "Faktúra " + number;
        }

        return title;
    }
}
