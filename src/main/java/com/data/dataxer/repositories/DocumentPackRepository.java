package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.DocumentPack;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DocumentPackRepository extends CrudRepository<DocumentPack, Long> {
    Optional<DocumentPack> findByIdAndDocumentId(Long packId, Long documentId);
}
