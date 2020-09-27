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

import java.util.ArrayList;
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
    public Cost store(Cost cost) {
        return this.costRepository.save(cost);
    }

    @Override
    public Cost update(Cost cost) {
        return this.costRepository.save(cost);
    }

    @Override
    public Page<Cost> paginate(Pageable pageable, String filters) {
        //TO-DO: handle request to repository by filters content

        List<Cost> costs = this.costRepository.findDefault(SecurityUtils.companyIds(), pageable);
        return new PageImpl<>(costs, pageable, costs.size());
    }


}
