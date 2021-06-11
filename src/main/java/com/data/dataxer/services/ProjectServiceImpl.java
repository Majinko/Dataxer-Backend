package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.domain.QTime;
import com.data.dataxer.models.dto.EvaluationDTO;
import com.data.dataxer.models.dto.ProjectManHoursDTO;
import com.data.dataxer.models.dto.ProjectTimePriceOverviewDTO;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.repositories.CostRepository;
import com.data.dataxer.repositories.ProjectRepository;
import com.data.dataxer.repositories.qrepositories.QCategoryRepository;
import com.data.dataxer.repositories.qrepositories.QInvoiceRepository;
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
import java.time.Month;
import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final QProjectRepository qProjectRepository;
    private final QTimeRepository qTimeRepository;
    private final CategoryRepository categoryRepository;
    private final QCategoryRepository qCategoryRepository;
    private final CostRepository costRepository;
    private final QInvoiceRepository qInvoiceRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, QProjectRepository qProjectRepository,
                              QTimeRepository qTimeRepository, CategoryRepository categoryRepository,
                              CostRepository costRepository, QCategoryRepository qCategoryRepository,
                              QInvoiceRepository qInvoiceRepository) {
        this.projectRepository = projectRepository;
        this.qProjectRepository = qProjectRepository;
        this.qTimeRepository = qTimeRepository;
        this.categoryRepository = categoryRepository;
        this.costRepository = costRepository;
        this.qCategoryRepository = qCategoryRepository;
        this.qInvoiceRepository = qInvoiceRepository;
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
        if (projectYears.size() < 1) {
            return response;
        }

        BigDecimal projectTotalCost = this.getProjectTotalCostForYears(projectYears.get(0), projectYears.get(projectYears.size() - 1));

        parentCategories.forEach(category -> {
            List<Tuple> categoryUsersData = this.qTimeRepository.getAllProjectUserCategoryData(id, parentCategoriesChildren.get(category), SecurityUtils.companyId());

            List<ProjectTimePriceOverviewDTO> responseValue = new ArrayList<>();
            categoryUsersData.forEach(userData -> {
                ProjectTimePriceOverviewDTO projectTimePriceOverviewDTO = this.getProjectOverviewData(StringUtils.getAppUserFullName(userData.get(QTime.time1.user.firstName), userData.get(QTime.time1.user.lastName)),
                        userData.get(QTime.time1.user.uid), userData.get(QTime.time1.time.sum()), userData.get(QTime.time1.price.sum()), projectYears, projectTotalCost);

                responseValue.add(projectTimePriceOverviewDTO);
            });
            if (responseValue.size() > 0) {
                response.put(category.getName(), responseValue);
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

    @Override
    public ProjectManHoursDTO getProjectManHours(Long id) {
        ProjectManHoursDTO projectManHoursDTO = new ProjectManHoursDTO();
        List<ProjectTimePriceOverviewDTO> projectTimePriceOverviewDTOList = new ArrayList<>();

        List<Integer> projectYears = this.getAllProjectYears(id);
        BigDecimal projectTotalCost = this.getProjectTotalCostForYears(projectYears.get(0), projectYears.get(projectYears.size() - 1));

        List<Tuple> userTimesPriceSums = this.qTimeRepository.getProjectUsersTimePriceSums(id, SecurityUtils.companyId());

        userTimesPriceSums.forEach(tuple -> {
            ProjectTimePriceOverviewDTO projectTimePriceOverviewDTO = this.getProjectOverviewData(StringUtils.getAppUserFullName(tuple.get(QTime.time1.user.firstName), tuple.get(QTime.time1.user.lastName)),
                    tuple.get(QTime.time1.user.uid), tuple.get(QTime.time1.time.sum()), tuple.get(QTime.time1.price.sum()), projectYears, projectTotalCost);

            projectManHoursDTO.setSumPriceNetto(projectManHoursDTO.getSumPriceNetto().add(projectTimePriceOverviewDTO.getPriceNetto()));
            projectManHoursDTO.setSumPriceBrutto(projectManHoursDTO.getSumPriceBrutto().add(projectTimePriceOverviewDTO.getPriceBrutto()));
            projectTimePriceOverviewDTOList.add(projectTimePriceOverviewDTO);
        });

        projectManHoursDTO.setProjectTimePriceOverviewDTOList(projectTimePriceOverviewDTOList);
        return projectManHoursDTO;
    }

    @Override
    public void projectEvaluationProfit(Long id) {
        EvaluationDTO evaluationDTO = new EvaluationDTO();
        BigDecimal projectInvoicesPriceSum = this.qInvoiceRepository.getProjectPriceSum(id, SecurityUtils.companyId());
        BigDecimal projectCostPriceSum = this.costRepository.getProjectCostSum(SecurityUtils.companyId());

        List<Tuple> projectUserData = this.qTimeRepository.getProjectUsersTimePriceSums(id, SecurityUtils.companyId());

        BigDecimal priceBruttoSum = BigDecimal.ZERO;
        Integer timeSum = 0;

        for (Tuple data:projectUserData) {
            priceBruttoSum = priceBruttoSum.add(data.get(QTime.time1.price.sum()));
            timeSum += data.get(QTime.time1.time.sum());
        }

        BigDecimal costs = projectCostPriceSum.add(priceBruttoSum);
        BigDecimal price = projectInvoicesPriceSum.subtract(costs);

        evaluationDTO.setProfit(price);
        evaluationDTO.setProfitManHour(price.divide(new BigDecimal((double) timeSum/60/60).setScale(2, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP));
        costs = costs.equals(BigDecimal.ZERO) ? BigDecimal.ONE : costs;
        evaluationDTO.setRebate(price.divide(costs).setScale(4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
    }

    private ProjectTimePriceOverviewDTO getProjectOverviewData(String userName, String uid, Integer seconds, BigDecimal priceNetto, List<Integer> projectYears, BigDecimal projectTotalCost) {
        BigDecimal costToHour = this.getUserCostToHour(projectYears.get(0), projectYears.get(projectYears.size() - 1), uid, projectTotalCost);

        return new ProjectTimePriceOverviewDTO(userName, seconds, costToHour, priceNetto);
    }

    private BigDecimal getUserCostToHour(Integer startYear, Integer endYear, String userUid, BigDecimal projectTotalCost) {
        int userTimeBetweenYears = this.qTimeRepository.getUserProjectTimeBetweenYears(startYear, endYear, userUid, SecurityUtils.companyId());
        int userActiveMonths = this.qTimeRepository.getUserActiveMonths(startYear, endYear, userUid, SecurityUtils.companyId()).size();
        BigDecimal allActiveMonths = new BigDecimal(this.qTimeRepository.getProjectAllUsersActiveMonth(startYear, endYear, SecurityUtils.companyId()).size());

        if (userActiveMonths == 0) {
            return new BigDecimal(userActiveMonths);
        }

        BigDecimal coefficient = new BigDecimal((this.convertTimeSecondsToHours(userTimeBetweenYears)) / userActiveMonths).setScale(2, RoundingMode.HALF_UP);

        if (coefficient.equals(BigDecimal.ZERO)) {
            coefficient = BigDecimal.ONE;
        }

        return projectTotalCost.divide(allActiveMonths, 2, RoundingMode.HALF_UP).divide(coefficient, 2, RoundingMode.HALF_UP);
    }

    private List<Integer> getAllProjectYears(Long id) {
        return this.qTimeRepository.getProjectYears(id, SecurityUtils.companyId());
    }

    private BigDecimal getProjectTotalCostForYears(Integer firstYear, Integer lastYear) {
        LocalDate firstYearStart = LocalDate.of(firstYear, Month.JANUARY, 1);
        LocalDate lastYearEnd = LocalDate.of(lastYear, Month.DECEMBER, 31);
        return this.costRepository.getProjectTotalCostBetweenYears(firstYearStart, lastYearEnd, Boolean.FALSE, Boolean.FALSE, SecurityUtils.companyId());
        //return this.qCostRepository.getProjectCostsForYears(firstYear, lastYear, costWithCategoryIds, SecurityUtils.companyId()).setScale(2, RoundingMode.HALF_UP);
    }

    private double convertTimeSecondsToHours(int time) {
        return (double) time/60/60;
    }

    private void evaluation(Long projectId, Category category) {
        /*Long parentCategoryId = category == null ? null : category.getId();

        List<AppUser> projectUsers = this.qProjectRepository.findAllProjectUsers(projectId, SecurityUtils.companyId());
        Map<String, List<ProjectTimePriceOverviewDTO>> projectCategoryOverview = this.getProjectCategoryOverview(projectId, parentCategoryId);
        List<Time> projectTimes = this.qTimeRepository.*/

    }
}
