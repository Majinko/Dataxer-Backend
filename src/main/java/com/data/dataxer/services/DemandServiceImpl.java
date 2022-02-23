package com.data.dataxer.services;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.models.domain.DemandPack;
import com.data.dataxer.models.domain.DemandPackItem;
import com.data.dataxer.repositories.DemandRepository;
import com.data.dataxer.repositories.qrepositories.QDemandRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemandServiceImpl implements DemandService {
    private final DemandRepository demandRepository;
    private final QDemandRepository qDemandRepository;

    public DemandServiceImpl(DemandRepository demandRepository, QDemandRepository qDemandRepository) {
        this.demandRepository = demandRepository;
        this.qDemandRepository = qDemandRepository;
    }

    @Override
    @Transactional
    public void store(Demand demand) {
        Demand storedDemand = this.demandRepository.save(demand);

        int demandPackPosition = 0;
        for (DemandPack demandPack : demand.getPacks()) {
            demandPack.setDemand(storedDemand);
            demandPack.setPosition(demandPackPosition);

            int demandPackItemPosition = 0;
            for (DemandPackItem demandPackItem : demandPack.getDemandPackItems()) {
                demandPackItem.setDemand(storedDemand);
                demandPackItem.setPosition(demandPackItemPosition);

                demandPackItemPosition++;
            }
        }
    }

    @Override
    public void update(Demand demand) {
        this.demandRepository.save(demand);
    }

    @Override
    public Page<Demand> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return qDemandRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.defaultProfileId());
    }

    private Demand getByIdSimple(Long id) {
        return this.demandRepository.findByIdAndAppProfileId(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Demand not found"));
    }

    @Override
    public Demand getById(Long id) {
        return this.qDemandRepository.getById(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Demand not found"));
    }

    @Override
    public void destroy(Long id) {
        Demand demand = this.getById(id);

        this.demandRepository.delete(demand);
    }
}
