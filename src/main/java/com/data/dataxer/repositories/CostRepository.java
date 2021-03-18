package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Cost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CostRepository extends CrudRepository<Cost, Long> {
    @Query("SELECT c FROM Cost c where c.isRepeated = true  AND c.deletedAt is null ")
    public List<Cost> findAllRepeated();

    @Query("SELECT cost FROM Cost cost LEFT JOIN Contact contact ON contact.id = cost.contact.id")
    List<Cost> paginate(Pageable pageable, List<Long> companyIds);

    Cost findByIdAndCompanyId(Long invoiceId, Long companyId);

    List<Cost> findAllByProjectIdAndCompanyId(Long projectId, Long companyId);
}
