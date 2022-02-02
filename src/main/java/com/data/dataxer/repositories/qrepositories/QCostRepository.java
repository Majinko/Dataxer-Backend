package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Cost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QCostRepository {
    Page<Cost> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);

    Optional<Cost> getById(Long id, Long appProfileId);

    Optional<Cost> getByIdWithRelation(Long id, Long appProfileId);

    List<Cost> getCostsWhereCategoryIdIn(List<Long> categoryIds, Integer year, Long appProfileId);

    BigDecimal getProjectCostsForYears(Integer firstYear, Integer lastYear, Long projectId, Long appProfileId);

    List<Integer> getCostsYears(Long appProfileId);

    BigDecimal getProjectTotalCostBetweenYears(LocalDate firstYearStart, LocalDate lastYearEnd, Boolean isInternal, Boolean isRepeated, List<Long> categoriesIdInProjectCost, Long appProfileId);

    Cost getLastCostByDayAndMonthAndYear(LocalDate date, Long companyId, Long defaultProfileId);

    Cost getLastCostByMonthAndYear(LocalDate date, Long companyId, Long defaultProfileId);

    Cost getLastCostByQuarterAndYear(LocalDate date, Long companyId, Long defaultProfileId);

    Cost getLastCostOfferByYear(LocalDate date, Long companyId, Long defaultProfileId);
}
