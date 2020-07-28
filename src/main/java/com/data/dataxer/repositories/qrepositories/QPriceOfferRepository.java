package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.PriceOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface QPriceOfferRepository {
    Page<PriceOffer> paginate(Pageable pageable, Map<String, String> filter, List<Long> companyIds);

    Optional<PriceOffer> getById(Long id, List<Long> companyIds);

    Optional<PriceOffer> getByIdSimple(Long id, List<Long> companyIds);
}
