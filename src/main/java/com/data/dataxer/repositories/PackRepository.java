package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Pack;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PackRepository extends CrudRepository<Pack, Long> {
    Optional<Pack> findByIdAndCompanyIdIn(Long id, List<Long> companyIds);

    @Query("select p from Pack p where p.id = ?1")
    Pack findById(Long id, List<Long> companyIds);

    @Query("SELECT p, c FROM Pack p LEFT JOIN Company c on c.id = p.company.id LEFT JOIN fetch p.packItems")
    Set<Pack> findAll(List<Long> companyIds);
}
