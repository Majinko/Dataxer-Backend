package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Cost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CostRepository extends CrudRepository<Cost, Long> {
    @Query("SELECT c FROM Cost c where c.isRepeated = true  AND c.deletedAt is null ")
    public List<Cost> findAllRepeated();

    List<Cost> findAllByAppProfileId(Long appProfileId);

    @Query("SELECT cost FROM Cost cost LEFT JOIN Contact contact ON contact.id = cost.contact.id WHERE cost.appProfile.id = ?2")
    List<Cost> paginate(Pageable pageable, Long appProfileId);

    Cost findByIdAndAppProfileId(Long invoiceId, Long appProfileId);

    List<Cost> findAllByProjectIdAndAppProfileId(Long projectId, Long appProfileId);

    @Query("SELECT sum(c.totalPrice) FROM Cost c WHERE c.deliveredDate >= ?1 AND c.deliveredDate <= ?2 AND c.isInternal = ?3 AND c.isRepeated = ?4 AND c.categories.size > 0 AND c.company.id = ?5")
    BigDecimal getProjectTotalCostBetweenYears(LocalDate firstYearStart, LocalDate lastYearEnd, Boolean isInternal, Boolean isRepeated, Long appProfileId);

    @Query("SELECT sum(c.totalPrice) FROM Cost c WHERE c.appProfile.id = ?1")
    BigDecimal getProjectCostSum(Long appProfileId);
}
