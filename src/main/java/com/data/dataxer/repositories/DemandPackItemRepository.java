package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.DemandPackItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DemandPackItemRepository extends CrudRepository<DemandPackItem, Long> {
    @Query("select i from DemandPackItem i left join fetch i.category left join fetch i.item where i.demand.id = ?1")
    List<DemandPackItem> findAllByDemandId(Long demandId);
}
