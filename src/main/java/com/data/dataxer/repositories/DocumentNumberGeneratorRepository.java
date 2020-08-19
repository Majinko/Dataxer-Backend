package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.DocumentNumberGenerator;
import org.springframework.data.repository.CrudRepository;

public interface DocumentNumberGeneratorRepository extends CrudRepository<DocumentNumberGenerator, Long> {
}
