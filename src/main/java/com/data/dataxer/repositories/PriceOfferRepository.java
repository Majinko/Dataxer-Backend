package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.PriceOffer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PriceOfferRepository extends CrudRepository<PriceOffer, Long> {
    List<PriceOffer> findAllByProjectIdAndCompanyIdIn(Long projectId, List<Long> companyIds);
}
