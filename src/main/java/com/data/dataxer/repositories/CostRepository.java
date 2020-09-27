package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Cost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CostRepository extends CrudRepository<Cost, Long> {

    @Query("SELECT c FROM Cost c WHERE c.title LIKE ?1 AND c.company.id IN ?2")
    public List<Cost> findByTitle(String title, List<Long> companyIds, Sort sort);

    @Query(value = "SELECT * FROM cost c WHERE c.company_id IN ?1", nativeQuery = true)
    public List<Cost> findDefault(List<Long> companyIds, Pageable pageable);

}
