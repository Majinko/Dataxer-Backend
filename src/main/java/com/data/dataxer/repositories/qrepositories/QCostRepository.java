package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Cost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QCostRepository {
    Page<Cost> paginate(Pageable pageable, List<Filter> costFilters, List<Long> companyIds);
}
