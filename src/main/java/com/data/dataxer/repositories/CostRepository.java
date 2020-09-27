package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Cost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CostRepository extends CrudRepository<Cost, Long> {

    @Query("SELECT c FROM Cost c WHERE c.title LIKE ?1 AND c.company.id IN ?2")
    public List<Cost> findByTitle(String title, List<Long> companyIds, Sort sort);

    @Query("SELECT c FROM Cost c WHERE c.company.id IN ?1")
    public List<Cost> findAllByCompanyIsIn(List<Long> companyIds, Sort sort, Pageable pageable);

}
