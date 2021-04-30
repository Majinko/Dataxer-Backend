package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QTime;
import com.data.dataxer.models.dto.ProjectManHoursDTO;
import com.data.dataxer.models.dto.ProjectTimePriceOverviewDTO;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.repositories.ProjectRepository;
import com.data.dataxer.repositories.qrepositories.QCostRepository;
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

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final QProjectRepository qProjectRepository;
    private final QTimeRepository qTimeRepository;
    private final CategoryRepository categoryRepository;
    private final QCostRepository qCostRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, QProjectRepository qProjectRepository,
                              QTimeRepository qTimeRepository, CategoryRepository categoryRepository,
                              QCostRepository qCostRepository) {
        this.projectRepository = projectRepository;
        this.qProjectRepository = qProjectRepository;
        this.qTimeRepository = qTimeRepository;
        this.categoryRepository = categoryRepository;
        this.qCostRepository = qCostRepository;
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
        categories.sort(Comparator.comparing(Category::getLft));
       // Collections.sort(categories, Comparator.comparing(Category::getDepth).thenComparing(Category::getPosition));

        return categories;
    }

    @Override
    public Map<String, List<ProjectTimePriceOverviewDTO>> getProjectCategoryOverview(Long id, Long categoryParentId) {
        HashMap<String, List<ProjectTimePriceOverviewDTO>> response = new HashMap<>();

        List<Category> parentCategories = categoryParentId == null
                ? this.categoryRepository.findAllByCompanyAndParentIsNull(SecurityUtils.companyId()).orElse(new ArrayList<>())
                : this.categoryRepository.findCategoryChildren(categoryParentId, SecurityUtils.companyId()).orElse(new ArrayList<>());
        HashMap<Category, List<Long>> parentCategoriesChildren = new HashMap<>();
        parentCategories.forEach(category -> {
            List<Long> childrenCategories = this.categoryRepository.findSubTreeIds(category.getId(), SecurityUtils.companyId());
            parentCategoriesChildren.put(category, childrenCategories);
        });

        List<Integer> projectYears = this.getAllProjectYears(id);
        List<Tuple> projectUsers = this.qTimeRepository.getAllProjectUsers(id, SecurityUtils.companyId());
        BigDecimal projectTotalCost = this.getProjectTotalCostForYears(projectYears.get(0), projectYears.get(projectYears.size() - 1));

        projectUsers.forEach(user -> {
            String fullName = StringUtils.getAppUserFullName(user.get(QTime.time1.user.firstName), user.get(QTime.time1.user.lastName));
            BigDecimal costToHour = this.getUserCostToHour(id, projectYears.get(0), projectYears.get(projectYears.size() - 1), user.get(QTime.time1.user.uid), projectTotalCost);

            List<ProjectTimePriceOverviewDTO> responseValue = new ArrayList<>();

            for (Category category : parentCategories) {
                Tuple userCategoryHoursAndPrice = this.qTimeRepository.getUserProjectCategoryHoursAndPrice(id, user.get(QTime.time1.user.uid), parentCategoriesChildren.get(category), SecurityUtils.companyId());
                if (userCategoryHoursAndPrice.get(QTime.time1.time.sum()) == null) {
                    //if user have no time with category we are continue in next iteration
                    continue;
                }

                ProjectTimePriceOverviewDTO projectTimePriceOverviewDTO = new ProjectTimePriceOverviewDTO();
                projectTimePriceOverviewDTO.setName(category.getName());
                projectTimePriceOverviewDTO.setHours(StringUtils.convertMinutesTimeToHoursString(userCategoryHoursAndPrice.get(QTime.time1.time.sum())));

                projectTimePriceOverviewDTO.setPriceNetto(userCategoryHoursAndPrice.get(QTime.time1.price.sum()));
                projectTimePriceOverviewDTO.setHourNetto(this.countHourNetto(userCategoryHoursAndPrice.get(QTime.time1.time.sum()), userCategoryHoursAndPrice.get(QTime.time1.price.sum())));

                projectTimePriceOverviewDTO.setHourBrutto(projectTimePriceOverviewDTO.getHourNetto().add(costToHour));
                projectTimePriceOverviewDTO.setPriceBrutto(
                        projectTimePriceOverviewDTO.getHourNetto().equals(projectTimePriceOverviewDTO.getHourBrutto()) ? projectTimePriceOverviewDTO.getPriceNetto() :
                                new BigDecimal((userCategoryHoursAndPrice.get(QTime.time1.time.sum()) / 3600)).multiply(projectTimePriceOverviewDTO.getHourBrutto()).setScale(2, RoundingMode.HALF_UP)
                );

                responseValue.add(projectTimePriceOverviewDTO);
            }

            response.put(fullName, responseValue);
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

    @Override
    public ProjectManHoursDTO getProjectManHours(Long id) {
        ProjectManHoursDTO projectManHoursDTO = new ProjectManHoursDTO();
        List<ProjectTimePriceOverviewDTO> projectTimePriceOverviewDTOList = new ArrayList<>();

        List<Integer> projectYears = this.getAllProjectYears(id);
        BigDecimal projectTotalCost = this.getProjectTotalCostForYears(projectYears.get(0), projectYears.get(projectYears.size() - 1));

        List<Tuple> userTimesPriceSums = this.qTimeRepository.getProjectUsersTimePriceSums(id, SecurityUtils.companyId());

        userTimesPriceSums.forEach(tuple -> {
            BigDecimal costToHour = this.getUserCostToHour(id, projectYears.get(0), projectYears.get(projectYears.size() - 1), tuple.get(QTime.time1.user.uid), projectTotalCost);

            ProjectTimePriceOverviewDTO projectTimePriceOverviewDTO = new ProjectTimePriceOverviewDTO();

            projectTimePriceOverviewDTO.setName(StringUtils.getAppUserFullName(tuple.get(QTime.time1.user.firstName), tuple.get(QTime.time1.user.lastName)));
            projectTimePriceOverviewDTO.setHours(StringUtils.convertMinutesTimeToHoursString(tuple.get(QTime.time1.time.sum())));

            projectTimePriceOverviewDTO.setPriceNetto(tuple.get(QTime.time1.price.sum()));
            projectManHoursDTO.setSumPriceNetto(projectManHoursDTO.getSumPriceNetto().add(tuple.get(QTime.time1.price.sum())));
            projectTimePriceOverviewDTO.setHourNetto(this.countHourNetto(tuple.get(QTime.time1.time.sum()), tuple.get(QTime.time1.price.sum())));

            projectTimePriceOverviewDTO.setHourBrutto(projectTimePriceOverviewDTO.getHourNetto().add(costToHour));
            projectTimePriceOverviewDTO.setPriceBrutto(
                    projectTimePriceOverviewDTO.getHourNetto().equals(projectTimePriceOverviewDTO.getHourBrutto()) ? projectTimePriceOverviewDTO.getPriceNetto() :
                            new BigDecimal((tuple.get(QTime.time1.time.sum()) / 3600)).multiply(projectTimePriceOverviewDTO.getHourBrutto()).setScale(2, RoundingMode.HALF_UP)
            );
            projectManHoursDTO.setSumPriceBrutto(projectManHoursDTO.getSumPriceBrutto().add(projectTimePriceOverviewDTO.getPriceBrutto()));
            projectTimePriceOverviewDTOList.add(projectTimePriceOverviewDTO);
        });

        projectManHoursDTO.setProjectTimePriceOverviewDTOList(projectTimePriceOverviewDTOList);
        return projectManHoursDTO;
    }

    private BigDecimal getUserCostToHour(Long projectId, Integer startYear, Integer endYear, String userUid, BigDecimal projectTotalCost) {
        int userTimeBetweenYears = this.qTimeRepository.getUserProjectTimeBetweenYears(projectId, startYear, endYear, userUid, SecurityUtils.companyId());
        int userActiveMonths = this.qTimeRepository.getUserActiveMonths(projectId, startYear, endYear, userUid, SecurityUtils.companyId()).size();
        BigDecimal allActiveMonths = new BigDecimal(this.qTimeRepository.getProjectAllUsersActiveMonth(projectId, startYear, endYear, SecurityUtils.companyId()).size());

        if (userActiveMonths == 0) {
            return new BigDecimal(userActiveMonths);
        }

        BigDecimal coefficient = new BigDecimal((userTimeBetweenYears/3600) / userActiveMonths);

        if (coefficient.equals(BigDecimal.ZERO)) {
            coefficient = BigDecimal.ONE;
        }

        return projectTotalCost.divide(allActiveMonths, 2, RoundingMode.HALF_UP).divide(coefficient, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal countHourNetto(Integer timeSum, BigDecimal priceSum) {
        BigDecimal minutePrice = priceSum.divide(new BigDecimal(timeSum/60), 2 , RoundingMode.HALF_UP);

        return minutePrice.multiply(new BigDecimal(60)).setScale(2, RoundingMode.HALF_UP);
    }

    private List<Integer> getAllProjectYears(Long id) {
        return this.qTimeRepository.getProjectYears(id, SecurityUtils.companyId());
    }

    private BigDecimal getProjectTotalCostForYears(Integer firstYear, Integer lastYear) {
        return this.qCostRepository.getProjectCostsForYears(firstYear, lastYear, SecurityUtils.companyId());
    }

}
