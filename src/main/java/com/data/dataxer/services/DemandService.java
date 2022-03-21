package com.data.dataxer.services;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.models.domain.DemandPackItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DemandService {
    void store(Demand demand);

    void update(Demand demandDTOtoDemand);

    Page<Demand> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    Demand getById(Long id);

    void destroy(Long id);

    List<DemandPackItem> getDemandPackItem(Long id);
}
