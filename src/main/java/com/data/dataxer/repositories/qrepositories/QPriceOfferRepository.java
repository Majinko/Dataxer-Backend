package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.PriceOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QPriceOfferRepository {
    Page<PriceOffer> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);

    Optional<PriceOffer> getById(Long id, Long appProfileId);

    Optional<PriceOffer> getByIdSimple(Long id, Long appProfileId);

    PriceOffer getLastPriceOffer(Long companyId, Long appProfileId);
}
