package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.Time;
import com.data.dataxer.models.dto.ProjectCategoriesOverviewDTO;
import com.data.dataxer.models.dto.ProjectCategoryUserOverviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ProjectService {
    Project store(Project project);

    void update(Project project);

    Project getById(Long id);

    Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    void destroy(Long id);

    List<Project> all();

    List<Project> search(String queryString);

    List<Category> getAllProjectCategories(Long projectId);

    List<Category> getAllProjectCategoriesOrderedByPosition(Long projectId);

    Map<String, List<ProjectCategoryUserOverviewDTO>> getProjectCategoryOverview(Long id, Long categoryParentId);

    List<Time> getProjectUsersTimesOverview(Long id, LocalDate dateFrom, LocalDate dateTo, String categoryName, String userUid);

    String getProjectTimeForThisYear(Long id);
}
