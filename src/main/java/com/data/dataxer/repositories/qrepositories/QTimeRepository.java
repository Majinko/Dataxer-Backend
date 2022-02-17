package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.Time;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QTimeRepository {
    Optional<Time> getById(Long id, Long userId, Long appProfileId);

    Optional<Time> getByIdSimple(Long id, Long userId, Long appProfileId);

    Page<Time> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long userId, Long appProfileId);

    List<Time> getHourOverviewForAllUsers(LocalDate fromDate, LocalDate toDate, Long appProfileId);

    List<Time> allForPeriod(String rqlFilter, Long userId, Long appProfileId);

    List<Tuple> getAllUserMonths(Long userId, Long appProfileId);

    List<Project> getAllUserProjects(Long userId, Long appProfileId);

    List<Tuple> getUserLastProjects(Long userId, Long limit, Long offset, Long appProfileId);

    List<Category> getProjectLastCategories(Long projectId, Long limit, Long appProfileId, String uid);

    LocalDate getUserFirstLastRecord(Long userId, Long appProfileId, Boolean last);

    Long getCountProjects(Long id, Long appProfileId);

    Integer sumUserTime(Long userId, Long appProfileId);

    List<Time> getAllTimeRecordsFromTo(LocalDate from, LocalDate to, Long appProfileId);

    List<Integer> getAllYears(Long appProfileId);

    List<Time> getProjectAllUsersTimes(Long id, Category category, LocalDate dateFrom, LocalDate dateTo, String userUid, Long appProfileId);

    Integer getTotalProjectTimeForYear(Long id, Integer year, Long appProfileId);

    List<Integer> getProjectYears(Long id, Long appProfileId);

    Tuple getUserProjectCategoryHoursAndPrice(Long projectId, String userUid, List<Long> categoryIds, Long appProfileId);

    Integer getUserProjectTimeBetweenYears(Integer startYear, Integer endYear, String uid, Long appProfileId);

    List<Tuple> getUserActiveMonths(Integer startYear, Integer endYear, String uid, Long appProfileId);

    List<Tuple> getProjectAllUsersActiveMonth(Integer startYear, Integer endYear, Long appProfileId);

    List<Tuple> getAllProjectUsers(Long id, Long companyId);

    List<Tuple> getProjectUsersTimePriceSums(Long id, Long appProfileId);

    List<Tuple> getAllProjectUserCategoryData(Long id, List<Long> longs, Long appProfileId);

    List<Time> getAllProjectTimesOrdered(Long projectId, Long appProfileId);

    List<Tuple> getAllUserTimesFromDateToDate(LocalDate processFromDate, LocalDate processToDate, Long appProfileId);

    List<Time> getAllProjectTimes(Long projectId, Long defaultProfileId);
}
