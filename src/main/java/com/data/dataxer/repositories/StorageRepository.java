package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Storage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StorageRepository extends CrudRepository<Storage, Long> {
    List<Storage> findAllByFileAbleIdAndFileAbleType(Long id, String type);

    Storage findByFileAbleIdAndFileAbleTypeAndCompanyIdInAndIsDefault(Long id, String type, List<Long> companyIds, boolean isDefault);

    Storage findByIdAndCompanyIdIn(Long id, List<Long> companyIds);
}
