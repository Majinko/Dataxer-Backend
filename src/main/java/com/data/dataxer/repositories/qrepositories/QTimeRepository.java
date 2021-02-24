package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.Time;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QTimeRepository {

    Optional<Time> getById(Long id, Long userId, Long companyId);

    Optional<Time> getByIdSimple(Long id, Long userId, Long companyId);

    Page<Time> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long userId,  Long companyId);

    List<Time> getHourOverviewForAllUsers(LocalDate fromDate, LocalDate toDate, Long companyId);

    List<Time> allForPeriod(LocalDate from, LocalDate to, Long userId, Long companyId);

    List<Tuple> getAllUserMonths(Long userId, Long companyId);

    List<Project> getAllUserProjects(Long userId, Long companyId);

    List<Project> getUserLastProjects(Long userId, Long limit, Long companyId);

    List<Time> getTimesForProjectCategoryOrderByDate(Long projectId, Long companyId);

    List<Time> getTimesForProjectCategoryOrderByPosition(Long projectId, Long companyId);
}
