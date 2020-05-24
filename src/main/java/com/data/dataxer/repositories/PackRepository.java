package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Pack;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PackRepository extends CrudRepository<Pack, Long> {
    Optional<Pack> findByIdAndCompanyIdIn(Long id, List<Long> companyIds);
}
