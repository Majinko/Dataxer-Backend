package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.models.enums.Periods;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QDemandRepository {
    Page<Demand> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);

    Demand getById(Long id, Long appProfileId);

    Demand getLastDemandByMonthAndYear(LocalDate date, Long companyId, Long defaultProfileId);

    Demand getLastDemandByQuarterAndYear(LocalDate date, Long companyId, Long defaultProfileId);

    Demand getLastDemandByDayAndMonthAndYear(LocalDate date, Long companyId, Long defaultProfileId);

    Demand getLastDemandByYear(LocalDate date, Long companyId, Long defaultProfileId);
}
