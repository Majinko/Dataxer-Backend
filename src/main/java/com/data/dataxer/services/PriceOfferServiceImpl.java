package com.data.dataxer.services;

import com.data.dataxer.models.domain.PriceOffer;
import com.data.dataxer.repositories.PriceOfferRepository;
import com.data.dataxer.repositories.qrepositories.QPriceOfferRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PriceOfferServiceImpl extends DocumentHelperService implements PriceOfferService {
    @Autowired
    private PriceOfferRepository priceOfferRepository;

    @Autowired
    private QPriceOfferRepository qPriceOfferRepository;

    @Override
    @Transactional
    public void store(PriceOffer priceOffer) {
        PriceOffer p = this.priceOfferRepository.save(priceOffer);

        this.setDocumentPackAndItems(p);
    }

    @Override
    @Transactional
    public void update(PriceOffer priceOffer) {
        PriceOffer p = (PriceOffer) this.setDocumentPackAndItems(priceOffer);

        p.setCompany(priceOffer.getCompany());

        this.priceOfferRepository.save(p);
    }

    @Override
    public Page<PriceOffer> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qPriceOfferRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.defaultProfileId());
    }

    @Override
    public PriceOffer getById(Long id) {
        return this.qPriceOfferRepository
                .getById(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Price offer not found"));
    }

    @Override
    public PriceOffer getByIdSimple(Long id) {
        return this.qPriceOfferRepository
                .getByIdSimple(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Price offer not found"));
    }

    @Override
    public void destroy(Long id) {
        priceOfferRepository.delete(this.getByIdSimple(id));
    }

    @Override
    public List<PriceOffer> findAllByProject(Long projectId, List<Long> companyIds) {
        return this.priceOfferRepository.findAllByProjectIdAndAndAppProfileId(projectId, SecurityUtils.defaultProfileId());
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
