package com.data.dataxer.services;

import com.data.dataxer.models.domain.PriceOffer;
import com.google.common.io.Files;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PriceOfferService {
    void store(PriceOffer priceOffer);

    void update(PriceOffer priceOffer);

    Page<PriceOffer> paginate(Pageable pageable);

    PriceOffer getById(Long id);

    void destroy(Long id);
}
