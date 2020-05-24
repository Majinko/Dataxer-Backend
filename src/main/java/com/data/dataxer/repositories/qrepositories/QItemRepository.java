package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Item;

import java.util.List;

public interface QItemRepository {
    Item getById(long id,  List<Long> companyIds);
}
