package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.DocumentNumberGenerator;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DocumentNumberGeneratorRepository extends CrudRepository<DocumentNumberGenerator, Long> {
    List<DocumentNumberGenerator> findAllByAppProfileId(Long appProfileId);
}
