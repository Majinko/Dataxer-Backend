package com.data.dataxer.services;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.models.domain.DemandPack;
import com.data.dataxer.models.domain.DemandPackItem;
import com.data.dataxer.repositories.DemandPackItemRepository;
import com.data.dataxer.repositories.DemandRepository;
import com.data.dataxer.repositories.qrepositories.QDemandRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DemandServiceImpl implements DemandService {
    @Autowired
    private DemandRepository demandRepository;
    @Autowired
    private QDemandRepository qDemandRepository;
    @Autowired
    private DemandPackItemRepository demandPackItemRepository;

    @Override
    @Transactional
    public void store(Demand demand) {
        Demand storedDemand = this.demandRepository.save(demand);

        int demandPackPosition = 0;

        for (DemandPack demandPack : demand.getPacks()) {
            demandPack.setDemand(storedDemand);
            demandPack.setPosition(demandPackPosition);

            int demandPackItemPosition = 0;
            for (DemandPackItem demandPackItem : demandPack.getPackItems()) {
                demandPackItem.setDemand(storedDemand);
                demandPackItem.setDemandPack(demandPack);
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

    @Override
    public Demand getById(Long id) {
        return this.qDemandRepository.getById(id, SecurityUtils.defaultProfileId());
    }

    @Override
    public void destroy(Long id) {
        Demand demand = this.getById(id);

        this.demandRepository.delete(demand);
    }

    @Override
    public List<DemandPackItem> getDemandPackItem(Long id) {
        return this.demandPackItemRepository.findAllByDemandId(id);
    }

    private Demand getByIdSimple(Long id) {
        return this.demandRepository.findByIdAndAppProfileId(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Demand not found"));
    }
}
