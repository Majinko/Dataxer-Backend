package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Item;

import java.util.List;
import java.util.Optional;

public interface QItemRepository {
    Item getById(long id,  List<Long> companyIds);

    Optional<List<Item>> findAllByTitleContainsAndCompanyIdIn(String q, List<Long> companyIds);
}
