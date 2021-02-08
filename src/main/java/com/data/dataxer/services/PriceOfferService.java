package com.data.dataxer.services;

import com.data.dataxer.models.domain.PriceOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PriceOfferService {
    void store(PriceOffer priceOffer);

    void update(PriceOffer priceOffer);

    Page<PriceOffer> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter);

    PriceOffer getById(Long id, Boolean disableFilter);

    PriceOffer getByIdSimple(Long id, Boolean disableFilter);

    void destroy(Long id);
}
