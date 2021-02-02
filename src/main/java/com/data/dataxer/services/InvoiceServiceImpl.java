package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.InvoiceRepository;
import com.data.dataxer.repositories.PaymentRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final QInvoiceRepository qInvoiceRepository;
    private final PaymentRepository paymentRepository;
    private final QPaymentRepository qPaymentRepository;
    private final DocumentRelationServiceImpl documentRelationService;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, QInvoiceRepository qInvoiceRepository,
                              DocumentRelationServiceImpl documentRelationService, QPaymentRepository qPaymentRepository,
                              PaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.qInvoiceRepository = qInvoiceRepository;
        this.documentRelationService = documentRelationService;
        this.paymentRepository = paymentRepository;
        this.qPaymentRepository = qPaymentRepository;
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

        this.store(invoice);
        this.storeRelation(invoice.getId(), oldInvoiceId);
    }

    @Override
    @Transactional
    public void storeTaxDocument(Invoice taxDocument, Long proformaInvoiceId) {
        Invoice i = this.invoiceRepository.save(taxDocument);

        this.setInvoicePackAndItems(i);
        this.storeAllTaxDocumentRelations(proformaInvoiceId, i.getId());
        this.updatePaymentsOfTaxDocument(proformaInvoiceId);
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
    public Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qInvoiceRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyIds());
    }

    @Override
    public Invoice getById(Long id) {
        return this.qInvoiceRepository
                .getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public Invoice getByIdSimple(Long id) {
        return this.qInvoiceRepository
                .getByIdSimple(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public void destroy(Long id) {
        this.invoiceRepository.delete(this.getByIdSimple(id));
    }

    @Override
    public void makePay(Long id, LocalDate payedDate) {
        Invoice invoice = this.qInvoiceRepository.getByIdSimple(id, SecurityUtils.companyIds()).orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setPaymentDate(payedDate);
        this.invoiceRepository.save(invoice);
    }

    @Override
    public Invoice generateTaxDocument(Long proformaInvoiceId) {
        Invoice proformaInvoice = this.getById(proformaInvoiceId);

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

    private List<DocumentPack> setTaxDocumentsPacks(Invoice proformaInvoice) {
        List<DocumentPack> documentPacks = new ArrayList<>();

        //priprava potrebnych dat**********
        BigDecimal payed = this.getPayedValueFromRelatedType(proformaInvoice.getId(), DocumentType.TAX_DOCUMENT);
        BigDecimal payments = this.getPaymentsValue(proformaInvoice.getId());

        //storing keys desc****************
        HashMap<Integer, BigDecimal> valuesForTaxes = getTaxesValuesMap(proformaInvoice.getPacks());
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

                DocumentPack taxDocumentPack = new DocumentPack();

                taxDocumentPack.setTax(sortedKeys.get(0));
                taxDocumentPack.setPrice(getPriceFromTotalPrice(totalPrice, keyTax));
                taxDocumentPack.setTotalPrice(totalPrice);
                taxDocumentPack.setTitle(this.generateTaxDocumentPackTitle(getNewestPaymentPayedDate(proformaInvoice.getId()), proformaInvoice.getVariableSymbol()));
                documentPacks.add(taxDocumentPack);
            }
            payed = payed.subtract(valuesForTaxes.get(keyTax));
            payments = payments.subtract(valuesForTaxes.get(keyTax));
        }

        return documentPacks;
    }

    @Override
    public Invoice generateSummaryInvoice(Long id) {
        Invoice proformaInvoice = this.getById(this.documentRelationService.getOriginalDocumentId(id));
        Invoice taxDocument = this.getById(id);
        Invoice summaryInvoice = new Invoice();
        BeanUtils.copyProperties(taxDocument, summaryInvoice,
                "id", "packs", "title", "note", "number", "state", "discount", "price",
                "totalPrice", "documentData", "createdDate", "variableSymbol", "headerComment",
                "paymentMethod", "invoiceType");
        summaryInvoice.setState(DocumentState.PAYED);
        summaryInvoice.setCreatedDate(LocalDate.now());
        taxDocument.setDocumentType(DocumentType.SUMMARY_INVOICE);
        this.setPropertiesForSummaryInvoice(proformaInvoice, taxDocument, summaryInvoice);
        return summaryInvoice;
    }

    @Override
    @Transactional
    public Invoice changeTypeAndSave(Long id, String type, String number) {
        Invoice originalInvoice = this.getById(id);
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
        return this.invoiceRepository.findAllByIdInAndCompanyIdIn(this.documentRelationService.getAllRelationDocumentIds(documentId), SecurityUtils.companyIds());
    }

    @Override
    @Transactional
    public Invoice duplicate(Long id) {
        Invoice originalInvoice = this.getById(id);
        Invoice duplicatedInvoice = new Invoice();
        BeanUtils.copyProperties(originalInvoice, duplicatedInvoice, "id", "packs");
        duplicatedInvoice.setPacks(this.duplicateDocumentPacks(originalInvoice.getPacks()));
        this.setInvoicePackAndItems(duplicatedInvoice);
        this.invoiceRepository.save(duplicatedInvoice);
        return duplicatedInvoice;
    }

    private HashMap<Integer, BigDecimal> getTaxesValuesMap(List<DocumentPack> documentPacks) {
        HashMap<Integer, BigDecimal> mappedTaxedValues = new HashMap<>();
        for (DocumentPack documentPack : documentPacks) {
            if (mappedTaxedValues.containsKey(documentPack.getTax())) {
                BigDecimal newValue = mappedTaxedValues.get(documentPack.getTax()).add(documentPack.getTotalPrice());
                mappedTaxedValues.replace(documentPack.getTax(), newValue);
            } else {
                mappedTaxedValues.put(documentPack.getTax(), documentPack.getTotalPrice());
            }
        }
        return mappedTaxedValues;
    }

    private BigDecimal getPaymentsValue(Long proformaInvoiceId) {
        BigDecimal paymentsValue = BigDecimal.ZERO;
        List<Payment> payments = this.qPaymentRepository.getPaymentsWithoutTaxDocumentByDocumentIdSortedByPayDate(proformaInvoiceId, SecurityUtils.companyIds());
        for (Payment payment : payments) {
            paymentsValue = paymentsValue.add(payment.getPayedValue());
        }
        return paymentsValue;
    }

    private BigDecimal getPayedValueFromRelatedType(Long proformaInvoiceId, DocumentType documentType) {
        BigDecimal payedValue = BigDecimal.ZERO;
        List<Invoice> relatedInvoices = this.documentRelationService.getAllRelationDocumentsByDocumentType(proformaInvoiceId, documentType);
        for (Invoice invoice : relatedInvoices) {
            payedValue = payedValue.add(invoice.getTotalPrice());
        }
        return payedValue;
    }

    private LocalDate getNewestPaymentPayedDate(Long proformaInvoiceId) {
        Payment payment = this.qPaymentRepository.getNewestWithoutTaxDocumentByDocumentId(proformaInvoiceId, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("No payment usable for tax document"));
        return payment.getPayedDate();
    }

    private String generateTaxDocumentPackTitle(LocalDate date, String variableSymbol) {
        return "Daňový doklad k prijatej platbe z " + date + " VS " + variableSymbol;
    }

    private Invoice setInvoicePackAndItems(Invoice invoice) {
        int packPosition = 0;

        for (DocumentPack documentPack : invoice.getPacks()) {
            documentPack.setDocumentId(invoice.getId());
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

    private void setPropertiesForSummaryInvoice(Invoice proforma, Invoice taxDocument, Invoice summaryInvoice) {
        List<DocumentPack> packs = proforma.getPacks();
        DocumentPack documentPack = new DocumentPack();
        documentPack.setTitle(generateSummaryInvoiceItemDescription(taxDocument.getNumber(), taxDocument.getCreatedDate(), taxDocument.getVariableSymbol()));
        summaryInvoice.setPacks(new ArrayList<>(List.of(documentPack)));
        summaryInvoice.setPrice(proforma.getPrice()
                .add(taxDocument.getPrice().multiply(BigDecimal.valueOf(-1))));
        summaryInvoice.setTotalPrice(proforma.getTotalPrice()
                .add(taxDocument.getTotalPrice().multiply(BigDecimal.valueOf(-1))));
    }

    private String generateSummaryInvoiceItemDescription(String number, LocalDate dateCreated, String variableSymbol) {
        return "Daňový doklad k prijatej platbe " + number +
                ", zo dňa " + dateCreated + ", variabilný symbol " + variableSymbol;
    }

    private BigDecimal getPriceFromTotalPrice(BigDecimal totalPrice, Integer tax) {
        double taxCoefficient = 1.0 + (tax / 100.0);
        return totalPrice.divide(BigDecimal.valueOf(taxCoefficient), 2, RoundingMode.HALF_UP);
    }

    private void storeAllTaxDocumentRelations(Long proformaInvoiceId, Long taxDocumentId) {
        this.storeRelation(proformaInvoiceId, taxDocumentId);
        this.storeRelation(taxDocumentId, proformaInvoiceId);

        List<Long> allRelationDocumentIds = this.documentRelationService.getAllRelationDocumentIds(proformaInvoiceId);
        for (Long relationDocumentId : allRelationDocumentIds) {
            this.storeRelation(taxDocumentId, relationDocumentId);
        }
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
        this.documentRelationService.storeDocumentRelation(documentRelation);
    }

    private void updatePaymentsOfTaxDocument(Long proformaInvoiceId) {
        List<Payment> payments = this.qPaymentRepository.getPaymentsWithoutTaxDocumentByDocumentIdSortedByPayDate(proformaInvoiceId, SecurityUtils.companyIds());
        for (Payment payment : payments) {
            payment.setTaxDocumentCreated(Boolean.TRUE);
            this.paymentRepository.save(payment);
        }
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
