package com.data.dataxer.services;

import com.data.dataxer.models.domain.PriceOffer;
import com.data.dataxer.repositories.PriceOfferRepository;
import com.data.dataxer.repositories.qrepositories.QPriceOfferRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PriceOfferServiceImpl extends DocumentHelperService implements PriceOfferService {
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

        this.setDocumentPackAndItems(p);
    }

    @Override
    public void update(PriceOffer priceOffer) {
        PriceOffer p = (PriceOffer) this.setDocumentPackAndItems(priceOffer);

        this.priceOfferRepository.save(p);
    }

    @Override
    public Page<PriceOffer> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qPriceOfferRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyId());
    }

    @Override
    public PriceOffer getById(Long id) {
        return this.qPriceOfferRepository
                .getById(id, SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Price offer not found"));
    }

    @Override
    public PriceOffer getByIdSimple(Long id) {
        return this.qPriceOfferRepository
                .getByIdSimple(id, SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Price offer not found"));
    }

    @Override
    public void destroy(Long id) {
        priceOfferRepository.delete(this.getByIdSimple(id));
    }

    @Override
    public List<PriceOffer> findAllByProject(Long projectId, List<Long> companyIds) {
        return this.priceOfferRepository.findAllByProjectIdAndCompanyIdIn(projectId, SecurityUtils.companyIds(companyIds));
    }

    @Override
    public PriceOffer duplicate(Long oldPriceOfferId) {
        PriceOffer original = this.getById(oldPriceOfferId);
        PriceOffer duplicate = new PriceOffer();
        BeanUtils.copyProperties(original, duplicate, "id", "packs");
        duplicate.setPacks(this.duplicateDocumentPacks(original.getPacks()));
        this.setDocumentPackAndItems(duplicate);

        return duplicate;
    }
}
