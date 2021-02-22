package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Time;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QTimeRepository {

    Optional<Time> getById(Long id, Long companyId);

    Optional<Time> getByIdSimple(Long id, Long companyId);

    Page<Time> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long userId,  Long companyId);

    List<Time> getHourOverviewForAllUsers(LocalDate fromDate, LocalDate toDate, Long companyId);

    List<Time> allForPeriod(LocalDate from, LocalDate to, Long companyId);

    List<Time> allUserTimesForPeriod(LocalDate from, LocalDate to, Long userId, Long companyId);

    List<Time> getUserLastProjects(Long userId, Long offset, Long limit, Long companyId);

    List<Time> getTimesForProjectCategoryOrderByDate(Long projectId, Long companyId);

    List<Time> getTimesForProjectCategoryOrderByPosition(Long projectId, Long companyId);
}
