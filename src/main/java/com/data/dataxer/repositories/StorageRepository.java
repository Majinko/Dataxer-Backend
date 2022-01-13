package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Storage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StorageRepository extends CrudRepository<Storage, Long> {
    List<Storage> findAllByFileAbleIdAndFileAbleType(Long id, String type);

    Storage findByFileAbleIdAndFileAbleTypeAndAppProfileIdAndIsDefault(Long id, String type, Long appProfileId, boolean isDefault);

    Storage findByIdAndAppProfileId(Long id, Long appProfileId);
}
