package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Storage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StorageRepository extends CrudRepository<Storage, Long> {
    List<Storage> findAllByFileAbleIdAndFileAbleType(Long id, String type);


    // todo pridat aj filtrovanie podla id firmy
    Storage findByFileAbleIdAndFileAbleTypeAndIsDefault(Long id, String type, boolean isDefault);
}
