package com.data.dataxer.services;

import com.data.dataxer.models.domain.Pack;
import com.data.dataxer.models.domain.PackItem;
import com.data.dataxer.repositories.PackRepository;
import com.data.dataxer.repositories.qrepositories.QPackRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackServiceImpl implements PackService {
    private final PackRepository packRepository;
    private final QPackRepository qPackRepository;

    public PackServiceImpl(PackRepository packRepository, QPackRepository qPackRepository) {
        this.packRepository = packRepository;
        this.qPackRepository = qPackRepository;
    }

    @Override
    public void store(Pack pack) {
        this.preparePackItems(pack);

        this.packRepository.save(pack);
    }

    @Override
    public void update(Pack pack) {
        this.preparePackItems(pack);

        this.packRepository.save(pack);
    }

    @Override
    public List<Pack> search(String q) {
        return qPackRepository.search(q, SecurityUtils.defaultProfileId());
    }

    @Override
    public Pack getByIdSimple(Long id) {
        return this.packRepository.findByIdAndAppProfileId(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Pack not found"));
    }

    @Override
    public Page<Pack> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qPackRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.defaultProfileId());
    }

    @Override
    public void destroy(Long id) {
        Pack pack = this.getByIdSimple(id);

        this.packRepository.delete(pack);
    }

    @Override
    public Pack getById(Long id) {
        return this.qPackRepository.getById(id, SecurityUtils.defaultProfileId());
    }

    private void preparePackItems(Pack pack){
        int position = 0;

        for (PackItem packItem : pack.getPackItems()){
            packItem.setPack(pack);
            packItem.setPosition(position);

            position++;
        }
    }
}
