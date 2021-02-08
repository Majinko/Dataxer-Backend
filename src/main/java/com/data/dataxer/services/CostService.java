package com.data.dataxer.services;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.enums.CostState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CostService {

    Cost store(Cost cost);

    Cost update(Cost cost);

    Page<Cost> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter);

    void taskExecute();

    Cost changeState(Long id, CostState state, Boolean disableFilter);

    void destroy(Long id);

    Cost getById(Long id, Boolean disableFilter);

    Cost getByIdWithRelation(Long id, Boolean disableFilter);

    Cost duplicate(Long id, Boolean disableFilter);
}
