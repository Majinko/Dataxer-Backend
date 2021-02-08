package com.data.dataxer.services;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.repositories.DemandRepository;
import com.data.dataxer.repositories.qrepositories.QDemandRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DemandServiceImpl implements DemandService {
    private final DemandRepository demandRepository;
    private final QDemandRepository qDemandRepository;

    public DemandServiceImpl(DemandRepository demandRepository, QDemandRepository qDemandRepository) {
        this.demandRepository = demandRepository;
        this.qDemandRepository = qDemandRepository;
    }

    @Override
    public void store(Demand demand) {
        this.demandRepository.save(demand);
    }

    @Override
    public void update(Demand demand) {
        this.demandRepository.save(demand);
    }

    @Override
    public Page<Demand> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter) {
        return qDemandRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.CompanyId(), disableFilter);
    }

    private Demand getByIdSimple(Long id) {
        return this.demandRepository.findByIdAndCompanyIdIn(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Demand not found"));
    }

    @Override
    public Demand getById(Long id, Boolean disableFilter) {
        return this.qDemandRepository.getById(id, SecurityUtils.CompanyId(), disableFilter)
                .orElseThrow(() -> new RuntimeException("Demand not found"));
    }

    @Override
    public void destroy(Long id) {
        Demand demand = this.getById(id, false);

        this.demandRepository.delete(demand);
    }
}
