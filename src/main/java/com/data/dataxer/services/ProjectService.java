package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.Time;
import com.data.dataxer.models.dto.EvaluationPreparationDTO;
import com.data.dataxer.models.dto.ProjectManHoursDTO;
import com.data.dataxer.models.dto.ProjectTimePriceOverviewCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

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

    List<ProjectTimePriceOverviewCategoryDTO> getProjectCategoryOverview(Long id, Long categoryParentId);

    List<Time> getProjectUsersTimesOverview(Long id, LocalDate dateFrom, LocalDate dateTo, String categoryName, String userUid);

    String getProjectTimeForThisYear(Long id);

    ProjectManHoursDTO getProjectManHours(Long id);

    EvaluationPreparationDTO evaluationPreparationProjectData(Long id);

    void addProfitUser(Long id, AppUser user);

    void removeProfitUser(Long id, AppUser user);

    List<Project> allHasCost();

    List<Project> allHasInvoice();

    List<Project> allHasPriceOffer();

    List<Project> allHasUserTime();
}
