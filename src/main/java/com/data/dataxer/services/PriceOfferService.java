package com.data.dataxer.services;

import com.data.dataxer.models.domain.PriceOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PriceOfferService {
    void store(PriceOffer priceOffer);

    void update(PriceOffer priceOffer);

    Page<PriceOffer> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    PriceOffer getById(Long id);

    PriceOffer getByIdSimple(Long id);

    void destroy(Long id);
}
