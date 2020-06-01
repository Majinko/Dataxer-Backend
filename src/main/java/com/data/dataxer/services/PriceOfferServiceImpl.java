package com.data.dataxer.services;

import com.data.dataxer.models.domain.PriceOffer;
import com.data.dataxer.repositories.PriceOfferRepository;
import com.data.dataxer.repositories.qrepositories.QPriceOfferRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PriceOfferServiceImpl implements PriceOfferService {
    private final PriceOfferRepository priceOfferRepository;
    private final QPriceOfferRepository qPriceOfferRepository;

    public PriceOfferServiceImpl(PriceOfferRepository priceOfferRepository, QPriceOfferRepository qPriceOfferRepository) {
        this.priceOfferRepository = priceOfferRepository;
        this.qPriceOfferRepository = qPriceOfferRepository;
    }

    @Override
    public void store(PriceOffer priceOffer) {
        this.priceOfferRepository.save(priceOffer);
    }

    @Override
    public void update(PriceOffer priceOffer) {
        this.priceOfferRepository.save(priceOffer);
    }

    @Override
    public Page<PriceOffer> paginate(Pageable pageable) {
        return this.qPriceOfferRepository.paginate(pageable, SecurityUtils.companyIds());
    }

    @Override
    public PriceOffer getById(Long id) {
        return this.qPriceOfferRepository
                .getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Price offer not found"));
    }

    @Override
    public void destroy(Long id) {
        priceOfferRepository.delete(this.getById(id));
    }
}
