package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.Time;
import com.data.dataxer.models.dto.MonthAndYearDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TimeService {
    Time store(Time time);

    void update(Time time);

    void destroy(Long id);

    Time getTimeById(Long id);

    Time getTimeByIdSimple(Long id);

    Page<Time> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    List<Time> allForPeriod(LocalDate from, LocalDate to);

    List<MonthAndYearDTO> getAllUserMonths(Long userId);

    List<Project> getAllUserProjects(Long userId);

    List<Project> getLastUserWorkingProjects(Long userId);

    List<Category> lastProjectCategories(Long projectId);

}
