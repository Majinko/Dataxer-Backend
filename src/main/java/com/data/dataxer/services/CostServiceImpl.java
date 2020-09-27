package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.repositories.CostRepository;
import com.data.dataxer.repositories.qrepositories.QCostRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CostServiceImpl implements CostService{

    private final CostRepository costRepository;
    private final QCostRepository qCostRepository;

    public CostServiceImpl(CostRepository costRepository, QCostRepository qCostRepository) {
        this.costRepository = costRepository;
        this.qCostRepository = qCostRepository;
    }

    @Override
    public void store(Cost cost) {
        this.costRepository.save(cost);
    }

    @Override
    public Cost update(Cost cost) {
        return this.costRepository.save(cost);
    }

    @Override
    public Page<Cost> paginate(Pageable pageable, List<Filter> costFilters) {
        List<Cost> costs = this.costRepository.findAllByCompanyIsIn(SecurityUtils.companyIds(), pageable.getSort(), pageable);
        return new PageImpl<>(costs, pageable, costs.size());
    }


}
