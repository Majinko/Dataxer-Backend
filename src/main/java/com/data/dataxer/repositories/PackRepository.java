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
    Optional<Pack> findByIdAndAppProfileId(Long id, Long appProfileId);

    @Query("select p from Pack p where p.id = ?1 and p.appProfile.id = ?2")
    Pack findById(Long id, Long appProfileId);

    @Query("SELECT p, ap FROM Pack p LEFT JOIN AppProfile ap on ap.id = p.appProfile.id LEFT JOIN fetch p.packItems")
    Set<Pack> findAll(Long appProfileId);
}
