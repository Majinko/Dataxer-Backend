package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.enums.CostState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CostService {

    Cost store(Cost cost);

    Cost update(Cost cost);

    Page<Cost> paginate(Pageable pageable, List<Filter> filters);

    void taskExecute();

    Cost changeState(Long id, CostState state);

    void destroy(Long id);

    Cost getById(Long id);

    Cost getByIdWithRelation(Long id);

    Cost duplicate(Long id);
}
