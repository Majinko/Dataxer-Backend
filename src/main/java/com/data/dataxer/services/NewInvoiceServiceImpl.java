package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.DocumentPack;
import com.data.dataxer.models.domain.DocumentPackItem;
import com.data.dataxer.models.domain.NewInvoice;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.NewInvoiceRepository;
import com.data.dataxer.repositories.qrepositories.QNewInvoiceRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewInvoiceServiceImpl implements NewInvoiceService {

    private final NewInvoiceRepository newInvoiceRepository;
    private final QNewInvoiceRepository qNewInvoiceRepository;

    public NewInvoiceServiceImpl(NewInvoiceRepository newInvoiceRepository, QNewInvoiceRepository qNewInvoiceRepository) {
        this.newInvoiceRepository = newInvoiceRepository;
        this.qNewInvoiceRepository = qNewInvoiceRepository;
    }

    @Override
    @Transactional
    public void store(NewInvoice invoice) {
        NewInvoice storedInvoice = this.newInvoiceRepository.save(invoice);
        this.savePacksAndItems(storedInvoice);
    }

    @Override
    public void update(NewInvoice invoice) {
        NewInvoice newInvoice = this.savePacksAndItems(invoice);
        this.newInvoiceRepository.save(newInvoice);
    }

    @Override
    public Page<NewInvoice> paginate(Pageable pageable, Filter[] filters) {
        return this.qNewInvoiceRepository.paginate(pageable, filters, SecurityUtils.companyIds());
    }

    @Override
    public NewInvoice getById(Long id) {
        return null;
    }

    @Override
    public NewInvoice getByIdSimple(Long id) {
        return null;
    }

    @Override
    public Page<NewInvoice> getByClient(Pageable pageable, Long contactId) {
        return null;
    }

    @Override
    public void destroy(Long id) {

    }

    @Override
    public void changeState(Long id, DocumentState documentState) {

    }

    @Override
    public NewInvoice duplicate(Long id) {
        return null;
    }

    private NewInvoice savePacksAndItems(NewInvoice storedInvoice) {
        int packPosition = 0;

        for (DocumentPack documentPack : storedInvoice.getPacks()) {
            documentPack.setDocumentId(storedInvoice.getId());
            documentPack.setType(DocumentType.INVOICE);
            documentPack.setPosition(packPosition);
            packPosition++;

            int packItemPosition = 0;
            for(DocumentPackItem documentPackItem : documentPack.getPackItems()) {
                documentPackItem.setPack(documentPack);
                documentPackItem.setPosition(packItemPosition);

                packItemPosition++;
            }
        }
        return storedInvoice;
    }
}
