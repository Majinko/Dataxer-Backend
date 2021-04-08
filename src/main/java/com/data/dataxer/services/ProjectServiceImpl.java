package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QTime;
import com.data.dataxer.models.dto.ProjectCategoryUserOverviewDTO;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.repositories.ProjectRepository;
import com.data.dataxer.repositories.qrepositories.QProjectRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.StringUtils;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final QProjectRepository qProjectRepository;
    private final QTimeRepository qTimeRepository;
    private final CategoryRepository categoryRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, QProjectRepository qProjectRepository,
                              QTimeRepository qTimeRepository, CategoryRepository categoryRepository) {
        this.projectRepository = projectRepository;
        this.qProjectRepository = qProjectRepository;
        this.qTimeRepository = qTimeRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Project store(Project project) {
        return this.projectRepository.save(project);
    }

    @Override
    public Project getById(Long id) {
        return this.qProjectRepository.getById(id, SecurityUtils.companyId());
    }

    @Override
    public void update(Project project) {
        this.projectRepository.save(project);
    }

    @Override
    public Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return qProjectRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyId());
    }

    @Override
    public void destroy(Long id) {
        this.projectRepository.delete(this.qProjectRepository.getById(id, SecurityUtils.companyId()));
    }

    @Override
    public List<Project> all() {
        return this.projectRepository.findAllByCompanyId(SecurityUtils.companyId());
    }

    @Override
    public List<Project> search(String queryString) {
        return this.qProjectRepository.search(SecurityUtils.companyId(), queryString);
    }

    @Override
    public List<Category> getAllProjectCategories(Long projectId) {
        return this.qProjectRepository.getById(projectId, SecurityUtils.companyId()).getCategories();
    }

    @Override
    public List<Category> getAllProjectCategoriesOrderedByPosition(Long projectId) {
        List<Category> categories = this.qProjectRepository.getById(projectId, SecurityUtils.companyId()).getCategories();
        Collections.sort(categories, Comparator.comparing(Category::getLft));
       // Collections.sort(categories, Comparator.comparing(Category::getDepth).thenComparing(Category::getPosition));

        return categories;
    }

    @Override
    public Map<String, List<ProjectCategoryUserOverviewDTO>> getProjectCategoryOverview(Long id, Long categoryParentId) {
        HashMap<String, List<ProjectCategoryUserOverviewDTO>> response = new HashMap<>();

        List<Category> categoriesCondition = categoryParentId == null
                ? this.categoryRepository.findAllByCompanyAndParentIsNull(SecurityUtils.companyId()).orElse(new ArrayList<>())
                : this.categoryRepository.findCategoryChildren(categoryParentId, SecurityUtils.companyId()).orElse(new ArrayList<>());

        List<Tuple> timeResult = this.qTimeRepository.getAllProjectUsersTimesWhereCategoryIn(categoriesCondition.stream().map(Category::getId).collect(Collectors.toList()),
                id, SecurityUtils.companyId());

        timeResult.forEach(time -> {
            ProjectCategoryUserOverviewDTO projectCategoryUserOverviewDTO = new ProjectCategoryUserOverviewDTO();
            projectCategoryUserOverviewDTO.setFullName(time.get(QTime.time1.user.firstName) + " " + time.get(QTime.time1.user.lastName));
            projectCategoryUserOverviewDTO.setHours(StringUtils.convertMinutesTimeToHoursString(time.get(QTime.time1.time.sum())));
            projectCategoryUserOverviewDTO.setHourNetto(this.countHourNetto(time.get(QTime.time1.time.sum()), time.get(QTime.time1.price.sum())));
            projectCategoryUserOverviewDTO.setPriceNetto(time.get(QTime.time1.price.sum()));
            if (response.containsKey(time.get(QTime.time1.category.name))) {
                response.get(time.get(QTime.time1.category.name)).add(projectCategoryUserOverviewDTO);
            } else {
                List<ProjectCategoryUserOverviewDTO> projectCategoryUserOverviewList = new ArrayList<>();
                projectCategoryUserOverviewList.add(projectCategoryUserOverviewDTO);
                response.put(time.get(QTime.time1.category.name), projectCategoryUserOverviewList);
            }
        });

        return response;
    }

    @Override
    public List<Time> getProjectUsersTimesOverview(Long id, LocalDate dateFrom, LocalDate dateTo, String categoryName, String userUid) {
        Category category = categoryName.equals("_all_") ? null :
                this.categoryRepository.findCategoryByName(categoryName, SecurityUtils.companyId()).orElse(null);

        return this.qTimeRepository.getProjectAllUsersTimes(id, category,dateFrom, dateTo, userUid, SecurityUtils.companyId());
    }

    @Override
    public String getProjectTimeForThisYear(Long id) {
        Integer currentYear = LocalDate.now().getYear();

        return StringUtils.convertMinutesTimeToHoursString(this.qTimeRepository.getTotalProjectTimeForYear(id, currentYear, SecurityUtils.companyId()));
    }

    private BigDecimal countHourNetto(Integer timeSum, BigDecimal priceSum) {
        BigDecimal minutePrice = priceSum.divide(new BigDecimal(timeSum/60));

        return minutePrice.multiply(new BigDecimal(60)).setScale(2, RoundingMode.UP);
    }


}
