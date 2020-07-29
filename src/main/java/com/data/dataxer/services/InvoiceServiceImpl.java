package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentPack;
import com.data.dataxer.models.domain.DocumentPackItem;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.InvoiceRepository;
import com.data.dataxer.repositories.qrepositories.QInvoiceRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final QInvoiceRepository qInvoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository, QInvoiceRepository qInvoiceRepository) {
        this.invoiceRepository = invoiceRepository;
        this.qInvoiceRepository = qInvoiceRepository;
    }

    @Override
    public void store(Invoice invoice) {
        Invoice invoiceUpdated = this.setInvoicePackAndItems(invoice);

        this.invoiceRepository.save(invoiceUpdated);
    }

    @Override
    public void update(Invoice invoice) {
        Invoice invoiceUpdated = this.setInvoicePackAndItems(invoice);

        this.invoiceRepository.save(invoiceUpdated);
    }

    @Override
    public Page<Invoice> paginate(Pageable pageable) {
        return this.qInvoiceRepository.paginate(pageable, SecurityUtils.companyIds());
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
    public Page<Invoice> getByState(Pageable pageable, String state) {
        return this.qInvoiceRepository.getByState(pageable, DocumentState.InvoiceStates.getStateByCode(state), SecurityUtils.companyIds());
    }

    @Override
    public void destroy(Long id) {
        this.invoiceRepository.delete(this.getByIdSimple(id));
    }

    @Override
    public void changeState(Invoice invoice) {
        this.qInvoiceRepository.getByIdSimple(invoice.getInvoiceId(), SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Invoice not found"))
                .setState(invoice.getState());
    }

    @Override
    public Page<Invoice> getByClient(Pageable pageable, Long contactId) {
        return this.qInvoiceRepository.getByClient(pageable, contactId, SecurityUtils.companyIds());
    }

    private Invoice setInvoicePackAndItems(Invoice invoice) {
        int packPosition = 0;

        for(DocumentPack documentPack : invoice.getPacks()) {
            documentPack.setDocumentId(invoice.getInvoiceId());
            documentPack.setType(DocumentType.INVOICE);
            documentPack.setPosition(packPosition);
            packPosition++;

            int packItemPosition = 0;

            for(DocumentPackItem packItem : documentPack.getPackItems()) {
                packItem.setPack(documentPack);
                packItem.setPosition(packItemPosition);

                packItemPosition++;
            }
        }

        return invoice;
    }
}