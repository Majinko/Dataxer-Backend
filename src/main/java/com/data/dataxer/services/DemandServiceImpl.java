package com.data.dataxer.services;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.repositories.DemandRepository;
import com.data.dataxer.repositories.qrepositories.QDemandRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
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
    public Page<Demand> paginate(Pageable pageable) {
        return qDemandRepository.paginate(pageable, SecurityUtils.companyIds());
    }

    private Demand getByIdSimple(Long id) {
        return this.demandRepository.findByIdAndCompanyIdIn(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Demand not found"));
    }

    @Override
    public Demand getById(Long id) {
        return this.qDemandRepository.getById(id, SecurityUtils.companyIds());
    }

    @Override
    public void destroy(Long id) {
        Demand demand = this.getById(id);

        this.demandRepository.delete(demand);
    }
}
