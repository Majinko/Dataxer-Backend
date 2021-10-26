package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Cost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QCostRepository {
    Page<Cost> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId);

    Optional<Cost> getById(Long id, List<Long> companyIds);

    Optional<Cost> getByIdWithRelation(Long id, List<Long> companyIds);

    List<Cost> getCostsWhereCategoryIdIn(List<Long> categoryIds, Integer year, Long companyId);

    BigDecimal getProjectCostsForYears(Integer firstYear, Integer lastYear, Long projectId, Long companyId);

    List<Integer> getCostsYears(Long companyId);

    BigDecimal getProjectTotalCostBetweenYears(LocalDate firstYearStart, LocalDate lastYearEnd, Boolean isInternal, Boolean isRepeated, List<Long> categoriesIdInProjectCost, Long companyId);
}
