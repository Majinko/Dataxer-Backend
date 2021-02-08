package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.PriceOfferRepository;
import com.data.dataxer.repositories.qrepositories.QPriceOfferRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PriceOfferServiceImpl implements PriceOfferService {
    private final PriceOfferRepository priceOfferRepository;
    private final QPriceOfferRepository qPriceOfferRepository;

    public PriceOfferServiceImpl(PriceOfferRepository priceOfferRepository, QPriceOfferRepository qPriceOfferRepository) {
        this.priceOfferRepository = priceOfferRepository;
        this.qPriceOfferRepository = qPriceOfferRepository;
    }

    @Override
    @Transactional
    public void store(PriceOffer priceOffer) {
        PriceOffer p = this.priceOfferRepository.save(priceOffer);

        this.setPriceOfferPackAndItems(p);
    }

    private PriceOffer setPriceOfferPackAndItems(PriceOffer priceOffer) {
        int packPosition = 0;

        for (DocumentPack documentPack : priceOffer.getPacks()) {
            documentPack.setDocument(priceOffer);
            documentPack.setType(DocumentType.PRICE_OFFER);
            documentPack.setPosition(packPosition);
            packPosition++;

            int packItemPosition = 0;

            for (DocumentPackItem packItem : documentPack.getPackItems()) {
                packItem.setPack(documentPack);
                packItem.setPosition(packItemPosition);

                packItemPosition++;
            }
        }

        return priceOffer;
    }

    @Override
    public void update(PriceOffer priceOffer) {
        PriceOffer p = this.setPriceOfferPackAndItems(priceOffer);

        this.priceOfferRepository.save(p);
    }

    @Override
    public Page<PriceOffer> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter) {
        return this.qPriceOfferRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.CompanyId(), disableFilter);
    }

    @Override
    public PriceOffer getById(Long id, Boolean disableFilter) {
        return this.qPriceOfferRepository
                .getById(id, SecurityUtils.CompanyId(), disableFilter)
                .orElseThrow(() -> new RuntimeException("Price offer not found"));
    }

    @Override
    public PriceOffer getByIdSimple(Long id, Boolean disableFilter) {
        return this.qPriceOfferRepository
                .getByIdSimple(id, SecurityUtils.CompanyId(), disableFilter)
                .orElseThrow(() -> new RuntimeException("Price offer not found"));
    }

    @Override
    public void destroy(Long id) {
        priceOfferRepository.delete(this.getByIdSimple(id, false));
    }
}
