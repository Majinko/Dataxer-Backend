package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.PriceOffer;
import com.google.common.io.Files;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface PriceOfferService {
    void store(PriceOffer priceOffer);

    void update(PriceOffer priceOffer);

    Page<PriceOffer> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    PriceOffer getById(Long id);

    PriceOffer getByIdSimple(Long id);

    void destroy(Long id);
}
