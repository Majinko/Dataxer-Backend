package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.DocumentPackItem;
import org.springframework.data.repository.CrudRepository;

public interface DocumentPackItemRepository extends CrudRepository<DocumentPackItem, Long> {
    DocumentPackItem findByIdAndPackIdAndAppProfileId(Long id, Long packId, Long profileId);
}
