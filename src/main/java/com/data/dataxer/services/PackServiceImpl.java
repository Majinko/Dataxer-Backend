package com.data.dataxer.services;

import com.data.dataxer.models.domain.Pack;
import com.data.dataxer.repositories.PackItemRepository;
import com.data.dataxer.repositories.PackRepository;
import com.data.dataxer.repositories.qrepositories.QPackRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
        pack.getItems().forEach(groupItem -> {
            groupItem.setPack(pack);
        });

        this.packRepository.save(pack);
    }

    @Override
    public void update(Pack pack) {
        pack.getItems().forEach(groupItem -> {
            groupItem.setPack(pack);
        });

        this.packRepository.save(pack);
    }

    @Override
    public Pack getByIdSimple(Long id) {
        return this.packRepository.findByIdAndCompanyIdIn(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Pack not found"));
    }

    @Override
    public Page<Pack> paginate(Pageable pageable) {
        return this.qPackRepository.paginate(pageable, SecurityUtils.companyIds());
    }

    @Override
    public void destroy(Long id) {
        Pack pack = this.getByIdSimple(id);

        this.packRepository.delete(pack);
    }

    @Override
    public Pack getById(Long id) {
        return this.qPackRepository.getById(id, SecurityUtils.companyIds());
    }
}
