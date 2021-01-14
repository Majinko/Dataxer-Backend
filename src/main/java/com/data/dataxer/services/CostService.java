package com.data.dataxer.services;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.enums.CostState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CostService {

    Cost store(Cost cost);

    Cost update(Cost cost);

    Page<Cost> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    void taskExecute();

    Cost changeState(Long id, CostState state);

    void destroy(Long id);

    Cost getById(Long id);

    Cost duplicate(Long id);
}
