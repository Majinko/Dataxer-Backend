package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Cost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CostRepository extends CrudRepository<Cost, Long> {
    @Query(value = "SELECT * FROM cost c WHERE c.company_id IN ?1 AND c.deleted_at IS NULL", nativeQuery = true)
    public List<Cost> findDefault(List<Long> companyIds, Pageable pageable);

    @Query(value = "SELECT * FROM cost c WHERE c.company_id IN ?1 AND c.deleted_at IS NULL AND (?2)", nativeQuery = true)
    public List<Cost> findWithFilter(List<Long> companyIds, String whereCondition, Pageable pageable);

    @Query(value = "SELECT * FROM cost c WHERE c.is_repeated = true AND c.deleted_at IS NULL", nativeQuery = true)
    public List<Cost> findAllRepeated();
}
