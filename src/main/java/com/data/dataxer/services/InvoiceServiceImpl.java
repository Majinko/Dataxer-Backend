package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.DocumentRelationsRepository;
import com.data.dataxer.repositories.InvoiceRepository;
import com.data.dataxer.repositories.qrepositories.QInvoiceRepository;
import com.data.dataxer.repositories.qrepositories.QPaymentRepository;
import com.data.dataxer.repositories.qrepositories.QPriceOfferRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.Helpers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl extends DocumentHelperService implements InvoiceService {

    public static final String PAYED_BY_DEPOSIT = "Uhradené zálohou";

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private QPriceOfferRepository qPriceOfferRepository;

    @Autowired
    private QInvoiceRepository qInvoiceRepository;

    @Autowired
    private QPaymentRepository qPaymentRepository;

    @Autowired
    private DocumentRelationsRepository documentRelationsRepository;

    @Override
    @Transactional
    public void store(Invoice invoice) {
        Invoice i = this.invoiceRepository.save(invoice);

        this.setDocumentPackAndItems(i);
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

        if (invoice.getDocumentType().equals(DocumentType.TAX_DOCUMENT) || invoice.getDocumentType().equals(DocumentType.SUMMARY_INVOICE)) {
            if (invoice.getDocumentType().equals(DocumentType.TAX_DOCUMENT)) {
                invoice.setState(DocumentState.PAYED);
                invoice.setPaymentDate(LocalDate.now());
            } else if (invoice.getDocumentType().equals(DocumentType.SUMMARY_INVOICE)) {
                if (invoice.getTotalPrice().compareTo(BigDecimal.ZERO) == 0) {
                    invoice.setState(DocumentState.PAYED);
                    invoice.setPaymentDate(LocalDate.now());
                }
            }
        }

        this.store(invoice);
        this.storeRelation(oldInvoiceId, invoice.getId());
        this.storeRelation(invoice.getId(), oldInvoiceId);
    }


    @Override
    @Transactional
    public void storeSummaryInvoice(Invoice summaryInvoice, Long taxDocumentId, Long proformaId) {
        Invoice i = this.invoiceRepository.save(summaryInvoice);

        this.setDocumentPackAndItems(i);
        this.storeAllSummaryInvoiceRelations(taxDocumentId, summaryInvoice.getId(), proformaId);
    }

    @Override
    public void update(Invoice invoice) {
        this.invoiceRepository.save((Invoice) this.cleanDocumentPacksBeforeUpdate(this.qInvoiceRepository.getById(invoice.getId(), SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"))));

        Invoice invoiceUpdated = (Invoice) this.setDocumentPackAndItems(invoice);

        if (!invoice.getDocumentType().equals(DocumentType.TAX_DOCUMENT)) {
            this.checkInvoicePayment(invoice);
        }

        this.invoiceRepository.save(invoiceUpdated);
    }

    private void checkInvoicePayment(Invoice invoice) {
        BigDecimal payedTotalPrice = this.qPaymentRepository.getPayedTotalPrice(invoice.getId(), invoice.getDocumentType());

        boolean isPayed = invoice.getTotalPrice().subtract(payedTotalPrice).setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) == 0;

        if (!isPayed) {
            invoice.setPaymentDate(null);
            invoice.setState(DocumentState.UNPAID);
        } else {
            invoice.setState(DocumentState.PAYED);
            invoice.setPaymentDate(LocalDate.now());
        }
    }

    @Override
    public Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qInvoiceRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.defaultProfileId());
    }

    @Override
    public Invoice getById(Long id) {
        return this.qInvoiceRepository
                .getById(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public Invoice getByIdWithoutFirm(Long id) {
        return this.qInvoiceRepository
                .getById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public Invoice getByIdSimple(Long id) {
        return this.qInvoiceRepository
                .getByIdSimple(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public void destroy(Long id) {
        this.invoiceRepository.delete(this.getByIdSimple(id));
    }

    @Override
    public void makePay(Long id, LocalDate payedDate) {
        Invoice invoice = this.qInvoiceRepository.getByIdSimple(id, SecurityUtils.defaultProfileId()).orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setPaymentDate(payedDate);
        this.invoiceRepository.save(invoice);
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
        return this.invoiceRepository.findAllByIdInAndAppProfileId(
                documentRelationsRepository.findAllByDocumentIdAndAppProfileId(documentId, SecurityUtils.defaultProfileId()).stream().map(DocumentRelation::getRelationDocumentId).collect(Collectors.toList()), SecurityUtils.defaultProfileId()
        );
    }

    @Override
    public List<Invoice> findAllByProject(Long projectId, List<Long> companyIds) {
        return this.invoiceRepository.findAllByProjectIdAndAppProfileId(projectId, SecurityUtils.defaultProfileId());
    }

    @Override
    public Invoice getInvoicesFromPriceOffer(Long id, String type) {
        DocumentType documentType = DocumentType.getTypeByName(type);
        PriceOffer priceOffer = this.qPriceOfferRepository.getById(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Price offer not found"));
        Invoice invoice = new Invoice();
        BeanUtils.copyProperties(priceOffer, invoice, "id", "title", "note", "number", "state",
                "createdDate", "createdAt", "updatedAt", "price", "totalPrice", "deliveredDate", "dueDate");

        switch (documentType) {
            case PROFORMA:
                invoice.setDocumentType(DocumentType.PROFORMA);
            case SUMMARY_INVOICE:
                invoice.setDocumentType(DocumentType.SUMMARY_INVOICE);
            case INVOICE:
            default:
                invoice.setDocumentType(DocumentType.INVOICE);
        }

        return invoice;
    }

    @Override
    @Transactional
    public Invoice duplicate(Long id) {
        Invoice originalInvoice = this.getById(id);
        Invoice duplicatedInvoice = new Invoice();
        BeanUtils.copyProperties(originalInvoice, duplicatedInvoice, "id", "packs");
        duplicatedInvoice.setPacks(this.duplicateDocumentPacks(originalInvoice.getPacks()));
        this.setDocumentPackAndItems(duplicatedInvoice);
        return duplicatedInvoice;
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
        taxDocument.setDeliveredDate(getNewestPaymentPayedDate(proformaInvoiceId));
        taxDocument.setDocumentType(DocumentType.TAX_DOCUMENT);
        taxDocument.setPacks(this.setTaxDocumentsPacks(proformaInvoice));

        return taxDocument;
    }

    @Override
    public Invoice generateSummaryInvoice(Long documentId, String type) {
        List<Long> allRelatedDocumentIds;
        Invoice summaryInvoice = new Invoice();
        List<DocumentPack> summaryInvoicePacks;

        switch (type) {
            case "priceOffer":
                PriceOffer priceOffer = this.qPriceOfferRepository.getById(documentId, SecurityUtils.defaultProfileId())
                        .orElseThrow(() -> new RuntimeException("Price offer not found"));
                allRelatedDocumentIds = this.documentRelationsRepository.findAllRelationDocuments(priceOffer.getId(), SecurityUtils.defaultProfileId())
                        .stream().map(DocumentRelation::getRelationDocumentId).collect(Collectors.toList());
                BeanUtils.copyProperties(priceOffer, summaryInvoice,
                        "id", "packs", "title", "note", "number", "state", "discount", "price",
                        "totalPrice", "documentData", "createdDate", "createdAt", "updatedAt");
                summaryInvoicePacks = new ArrayList<>(priceOffer.getPacks());
                break;
            case "invoice":
                Invoice invoice = this.qInvoiceRepository.getById(documentId, SecurityUtils.defaultProfileId()).orElseThrow(
                        () -> new RuntimeException("Invoice not found"));
                allRelatedDocumentIds = this.documentRelationsRepository.findAllRelationDocuments(invoice.getId(), SecurityUtils.defaultProfileId())
                        .stream().map(DocumentRelation::getRelationDocumentId).collect(Collectors.toList());
                BeanUtils.copyProperties(invoice, summaryInvoice,
                        "id", "packs", "title", "note", "number", "state", "discount", "price",
                        "totalPrice", "documentData", "createdDate", "variableSymbol", "headerComment",
                        "paymentMethod", "invoiceType", "createdAt", "updatedAt");
                summaryInvoicePacks = new ArrayList<>(invoice.getPacks());
                break;
            case "taxDocument":
            default:
                Invoice proformaInvoice = this.getOriginalProformaInvoiceFromTaxDocument(documentId);
                allRelatedDocumentIds = this.documentRelationsRepository.findAllRelationDocuments(proformaInvoice.getId(), SecurityUtils.defaultProfileId())
                        .stream().map(DocumentRelation::getRelationDocumentId).collect(Collectors.toList());
                BeanUtils.copyProperties(proformaInvoice, summaryInvoice,
                        "id", "packs", "title", "note", "number", "state", "discount", "price",
                        "totalPrice", "documentData", "createdDate", "variableSymbol", "headerComment",
                        "paymentMethod", "invoiceType", "createdAt", "updatedAt");
                summaryInvoicePacks = new ArrayList<>(proformaInvoice.getPacks());
                break;
        }

        List<Invoice> taxDocuments = this.qInvoiceRepository.getAllInvoicesIdInAndType(allRelatedDocumentIds, DocumentType.TAX_DOCUMENT, SecurityUtils.defaultProfileId());

        summaryInvoice.setDocumentType(DocumentType.SUMMARY_INVOICE);

        HashMap<Integer, DocumentPack> taxesPacks = new HashMap<>();
        for (Invoice taxDocument : taxDocuments) {
            for (DocumentPack documentPack : taxDocument.getPacks()) {
                DocumentPack responsePack = this.generateDocumentPackForSummaryInvoice(documentPack, taxDocument.getNumber(), taxDocument.getCreatedDate(), taxDocument.getVariableSymbol());
                if (taxesPacks.containsKey(documentPack.getTax())) {
                    DocumentPack pack = taxesPacks.get(documentPack.getTax());
                    pack.setPrice(pack.getPrice().add(responsePack.getPrice()));
                    pack.setTotalPrice(pack.getTotalPrice().add(responsePack.getTotalPrice()));
                    List<DocumentPackItem> items = new ArrayList<>(pack.getPackItems());
                    items.addAll(new ArrayList<>(responsePack.getPackItems()));
                    pack.setPackItems(items);
                    pack.setShowItems(true);
                    taxesPacks.replace(documentPack.getTax(), pack);
                } else {
                    taxesPacks.put(documentPack.getTax(), responsePack);
                }
            }
        }

        summaryInvoicePacks.addAll(new ArrayList<>(taxesPacks.values()));
        summaryInvoice.setPacks(summaryInvoicePacks);

        return summaryInvoice;
    }

    private Invoice getOriginalProformaInvoiceFromTaxDocument(Long taxDocumentId) {
        List<Invoice> invoices = this.invoiceRepository.findAllByIdInAndAppProfileId(
                this.documentRelationsRepository.findOriginalDocumentIdByRelative(taxDocumentId, SecurityUtils.defaultProfileId()).stream().map(DocumentRelation::getDocumentId).collect(Collectors.toList()), SecurityUtils.defaultProfileId()
        );
        for (Invoice invoice : invoices) {
            if (invoice.getDocumentType().equals(DocumentType.PROFORMA)) {
                // todo fix next time
                return this.getById(invoice.getId());
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
        //zoradene zaklady pre dph so zlavou (suma je s dph)
        LinkedHashMap<Integer, BigDecimal> sortedValuesForTaxesWithDiscount = Helpers.sortHashmapAndSubtractDiscount(valuesForTaxes, proformaInvoice.getDiscount());

        for (Integer keyTax : sortedValuesForTaxesWithDiscount.keySet()) {
            if (payments.compareTo(BigDecimal.ZERO) > 0 && sortedValuesForTaxesWithDiscount.get(keyTax).compareTo(payed) > 0) {
                BigDecimal totalPrice = payments.compareTo(sortedValuesForTaxesWithDiscount.get(keyTax)) > 0 ? sortedValuesForTaxesWithDiscount.get(keyTax) : payments;
                if (payed.compareTo(BigDecimal.ZERO) > 0) {
                    totalPrice = totalPrice.subtract(payed);
                }

                documentPacks.add(generateDocumentPackForTaxDocument(proformaInvoice, keyTax, totalPrice));
            }

            payed = payed.subtract(sortedValuesForTaxesWithDiscount.get(keyTax));
            payments = payments.subtract(sortedValuesForTaxesWithDiscount.get(keyTax));
        }

        return documentPacks;
    }

    private DocumentPack generateDocumentPackForTaxDocument(Invoice proformaInvoice, Integer tax, BigDecimal totalPrice) {
        DocumentPack taxDocumentPack = new DocumentPack();

        taxDocumentPack.setTax(tax);
        taxDocumentPack.setPrice(totalPrice.divide(new BigDecimal(1.0 + tax/100.0), 2, RoundingMode.HALF_UP));
        taxDocumentPack.setTotalPrice(totalPrice);
        taxDocumentPack.setTitle("Daňový doklad k prijatej platbe");
        taxDocumentPack.setShowItems(true);

        taxDocumentPack.setPackItems(List.of(generateDocumentPackItemForTaxDocument(proformaInvoice, tax, totalPrice)));

        return taxDocumentPack;
    }

    private DocumentPack generateDocumentPackForSummaryInvoice(DocumentPack taxDocumentPack, String taxDocumentNumber,
                                                               LocalDate taxDocumentCreated, String taxDocumentVariableSymbol) {
        DocumentPack summaryInvoicePack = new DocumentPack();

        summaryInvoicePack.setTitle(PAYED_BY_DEPOSIT);
        summaryInvoicePack.setTax(taxDocumentPack.getTax());
        summaryInvoicePack.setPrice(taxDocumentPack.getPrice().negate());
        summaryInvoicePack.setTotalPrice(taxDocumentPack.getTotalPrice().negate());
        summaryInvoicePack.setShowItems(true);

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
        documentPackItem.setPrice(totalPrice.divide(new BigDecimal(1.0 + tax/100.0), 2, RoundingMode.HALF_UP));
        documentPackItem.setTotalPrice(totalPrice);

        return documentPackItem;
    }

    private DocumentPackItem generateDocumentPackItemForSummaryInvoice(DocumentPack taxDocumentPack, String taxDocumentNumber,
                                                                       LocalDate taxDocumentCreated, String taxDocumentVariableSymbol) {
        DocumentPackItem documentPackItem = new DocumentPackItem();

        documentPackItem.setTitle(this.generateSummaryInvoicePackTitle(taxDocumentNumber, taxDocumentCreated, taxDocumentVariableSymbol));
        documentPackItem.setQty(1f);
        documentPackItem.setTax(taxDocumentPack.getTax());
        documentPackItem.setPrice(taxDocumentPack.getPrice().negate());
        documentPackItem.setTotalPrice(taxDocumentPack.getTotalPrice().negate());
        documentPackItem.setDiscount(BigDecimal.valueOf(0));

        return documentPackItem;
    }

    // return all item in invoices
    // dont add tax document pack
    @Override
    public List<DocumentPackItem> getInvoiceItems(List<DocumentPack> packs) {
        List<DocumentPackItem> documentPackItems = new ArrayList<>();

        packs.forEach(pack -> {
            if (!PAYED_BY_DEPOSIT.equals(pack.getTitle())) {
                if (pack.getCustomPrice() != null && pack.getCustomPrice()) {
                    DocumentPackItem tmpItem = new DocumentPackItem();
                    tmpItem.setTax(pack.getTax());
                    tmpItem.setPrice(pack.getPrice());
                    tmpItem.setTotalPrice(pack.getTotalPrice());
                    documentPackItems.add(tmpItem);
                } else {
                    documentPackItems.addAll(pack.getPackItems());
                }
            }
        });

        return documentPackItems;
    }

    /**
     * Spocita pre jednotlive dane zaklad dane (price) so zlavou
     */
    @Override
    public HashMap<Integer, BigDecimal> getTaxesValuesMapWithDiscount(List<DocumentPackItem> documentPackItems) {
        HashMap<Integer, BigDecimal> mappedTaxedValues = new HashMap<>();

        for (DocumentPackItem documentPackItem : documentPackItems) {
            if (mappedTaxedValues.containsKey(documentPackItem.getTax())) {
                //ak ma item zlavu tak ju odpocitame
                BigDecimal newValue;
                if (documentPackItem.getDiscount() != null && documentPackItem.getDiscount().compareTo(BigDecimal.ZERO) == 1) {
                    newValue = mappedTaxedValues.get(documentPackItem.getTax()).add(
                            documentPackItem.getPrice() != null
                                    ? documentPackItem.getPrice().add(documentPackItem.countPriceDiscount())
                                    .multiply(new BigDecimal(documentPackItem.getQty() != null ? documentPackItem.getQty() : 1)).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                } else {
                    newValue = mappedTaxedValues.get(documentPackItem.getTax()).add(
                            documentPackItem.getPrice() != null
                                    ? documentPackItem.getPrice().multiply(new BigDecimal(documentPackItem.getQty() != null ? documentPackItem.getQty() : 1))
                                    .setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                }
                mappedTaxedValues.replace(documentPackItem.getTax(), newValue);
            } else {
                BigDecimal price;
                if (documentPackItem.getDiscount() != null && documentPackItem.getDiscount().compareTo(BigDecimal.ZERO) == 1) {
                    price = documentPackItem.getPrice() != null
                            ? documentPackItem.getPrice().add(documentPackItem.countPriceDiscount()).multiply(new BigDecimal(documentPackItem.getQty() != null ? documentPackItem.getQty() : 1))
                            .setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                } else {
                    price = documentPackItem.getPrice() != null
                            ? documentPackItem.getPrice().multiply(new BigDecimal(documentPackItem.getQty() != null ? documentPackItem.getQty() : 1)).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                }
                mappedTaxedValues.put(documentPackItem.getTax(), price);
            }
        }
        return mappedTaxedValues;
    }

    private HashMap<Integer, BigDecimal> getTaxesValuesMap(List<DocumentPackItem> documentPackItems) {
        HashMap<Integer, BigDecimal> mappedTaxedValues = new HashMap<>();

        for (DocumentPackItem documentPackItem : documentPackItems) {
            if (mappedTaxedValues.containsKey(documentPackItem.getTax())) {
                BigDecimal newValue = mappedTaxedValues.get(documentPackItem.getTax()).add(
                            documentPackItem.getTotalPrice() != null && documentPackItem.getTotalPrice().compareTo(BigDecimal.ZERO) != -1
                                    ? documentPackItem.getTotalPrice().multiply(new BigDecimal(documentPackItem.getQty() != null ? documentPackItem.getQty() : 1))
                                    .setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
                mappedTaxedValues.replace(documentPackItem.getTax(), newValue);
            } else {
                BigDecimal price = documentPackItem.getTotalPrice() != null && documentPackItem.getTotalPrice().compareTo(BigDecimal.ZERO) != -1
                            ? documentPackItem.getTotalPrice().multiply(new BigDecimal(documentPackItem.getQty() != null ? documentPackItem.getQty() : 1)).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                mappedTaxedValues.put(documentPackItem.getTax(), price);
            }
        }
        return mappedTaxedValues;
    }

    @Override
    public HashMap<Integer, BigDecimal> getInvoicePayedTaxesValuesMap(List<DocumentPack> packs) {
        HashMap<Integer, BigDecimal> mappedPayedTaxedValues = new HashMap<>();
        for (DocumentPack pack : packs) {
            if (PAYED_BY_DEPOSIT.equals(pack.getTitle())) {
                if (mappedPayedTaxedValues.containsKey(pack.getTax())) {
                    BigDecimal newValue = mappedPayedTaxedValues.get(pack.getTax()).add(pack.getPrice());
                    mappedPayedTaxedValues.replace(pack.getTax(), newValue);
                } else {
                    mappedPayedTaxedValues.put(pack.getTax(), pack.getPrice());
                }
            }
        }
        return mappedPayedTaxedValues;
    }

    @Override
    public BigDecimal getTaxDocumentPayedValue(List<DocumentPack> packs) {
        BigDecimal result = BigDecimal.ZERO;
        for (DocumentPack pack : packs) {
            result = result.add(pack.getTotalPrice());
        }
        return result;
    }

    @Override
    public HashMap<Integer, BigDecimal> getTaxPayedTaxesValuesMap(List<DocumentPack> packs) {
        HashMap<Integer, BigDecimal> mappedPayedTaxedValues = new HashMap<>();
        for (DocumentPack pack : packs) {
            if (PAYED_BY_DEPOSIT.equals(pack.getTitle())) {
                if (mappedPayedTaxedValues.containsKey(pack.getTax())) {
                    BigDecimal newValue = mappedPayedTaxedValues.get(pack.getTax()).add(pack.getTotalPrice().subtract(pack.getPrice()));
                    mappedPayedTaxedValues.replace(pack.getTax(), newValue);
                } else {
                    mappedPayedTaxedValues.put(pack.getTax(), pack.getTotalPrice().subtract(pack.getPrice()));
                }
            }
        }
        return mappedPayedTaxedValues;
    }

    private BigDecimal getPaymentsValue(Long proformaInvoiceId) {
        BigDecimal paymentsValue = BigDecimal.ZERO;
        List<Payment> payments = this.qPaymentRepository.getPaymentsByDocumentIdSortedByPayDate(proformaInvoiceId, SecurityUtils.defaultProfileId());

        for (Payment payment : payments) {
            paymentsValue = paymentsValue.add(payment.getPayedValue());
        }
        return paymentsValue;
    }

    private BigDecimal getPayedValueFromRelatedType(Long proformaInvoiceId, DocumentType documentType) {
        BigDecimal payedValue = BigDecimal.ZERO;
        List<Invoice> relatedInvoices = this.invoiceRepository.findAllByDocumentTypeAndIdInAndAppProfileId(
                documentType,
                documentRelationsRepository.findAllByDocumentIdAndAppProfileId(proformaInvoiceId, SecurityUtils.defaultProfileId()).stream().map(DocumentRelation::getRelationDocumentId).collect(Collectors.toList()),
                SecurityUtils.defaultProfileId()
        );

        for (Invoice invoice : relatedInvoices) {
            payedValue = payedValue.add(invoice.getTotalPrice());
        }
        return payedValue;
    }

    private LocalDate getNewestPaymentPayedDate(Long proformaInvoiceId) {
        Payment payment = this.qPaymentRepository.getNewestByDocumentId(proformaInvoiceId, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("No payment usable for tax document"));
        return payment.getPayedDate();
    }

    private String generateTaxDocumentPackTitle(LocalDate date, Invoice proformaInvoice) {
        return "Na základe zálohovej faktúry " + proformaInvoice.getTitle() + " uhradenej " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " VS " + proformaInvoice.getVariableSymbol();
    }

    private String generateSummaryInvoicePackTitle(String taxDocumentNumber, LocalDate taxDocumentCreatedDate, String taxDocumentVariableSymbol) {
        return "Daňový doklad k prijatej platbe " + taxDocumentNumber + ", zo dňa "
                + taxDocumentCreatedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ", VS " + taxDocumentVariableSymbol;
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
        DocumentRelation documentRelation = new DocumentRelation();
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
