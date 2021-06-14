package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.QTime;
import com.data.dataxer.models.domain.Time;
import com.data.dataxer.models.dto.EvaluationDTO;
import com.data.dataxer.models.dto.ProjectManHoursDTO;
import com.data.dataxer.models.dto.ProjectTimePriceOverviewCategoryDTO;
import com.data.dataxer.models.dto.UserTimePriceOverviewDTO;
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
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final QProjectRepository qProjectRepository;
    private final QTimeRepository qTimeRepository;
    private final CategoryRepository categoryRepository;
    private final CostRepository costRepository;
    private final QCategoryRepository qCategoryRepository;
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

        return categories;
    }

    @Override
    public List<ProjectTimePriceOverviewCategoryDTO> getProjectCategoryOverview(Long id, Long categoryParentId) {
        List<ProjectTimePriceOverviewCategoryDTO> response = new ArrayList<>();

        List<Category> parentCategories = this.prepareParentCategories(categoryParentId);
        Map<Category, List<Long>> parentCategoriesChildren = this.prepareParentCategoriesChildren(parentCategories);

        parentCategories.forEach(category -> {
            List<Tuple> categoryUsersData = this.qTimeRepository.getAllProjectUserCategoryData(id, parentCategoriesChildren.get(category), SecurityUtils.companyId());

            List<UserTimePriceOverviewDTO> responseValue = new ArrayList<>();

            categoryUsersData.forEach(userData -> {
                UserTimePriceOverviewDTO projectTimePriceOverviewDTO = new UserTimePriceOverviewDTO();

                prepareProjectTimePriceOverviewDTO(id, userData, projectTimePriceOverviewDTO);

                responseValue.add(projectTimePriceOverviewDTO);
            });

            if (responseValue.size() > 0) {
                response.add(new ProjectTimePriceOverviewCategoryDTO(category.getId(), category.getName(), responseValue.stream().mapToInt(UserTimePriceOverviewDTO::getHours).sum(), responseValue));
            }
        });

        return response;
    }

    private List<Category> prepareParentCategories(Long categoryParentId) {
        return categoryParentId == null
                ? this.qCategoryRepository.getAllRootCategories(SecurityUtils.companyId()).orElse(new ArrayList<>())
                : this.qCategoryRepository.getChildren(categoryParentId, SecurityUtils.companyId()).orElse(new ArrayList<>());
    }

    private Map<Category, List<Long>> prepareParentCategoriesChildren(List<Category> parentCategories) {
        HashMap<Category, List<Long>> parentCategoriesChildren = new HashMap<>();

        parentCategories.forEach(category -> {
            List<Long> childrenCategories = this.qCategoryRepository.findSubTree(category, SecurityUtils.companyId())
                    .stream().map(Category::getId).collect(Collectors.toList());
            parentCategoriesChildren.put(category, childrenCategories);
        });

        return parentCategoriesChildren;
    }

    private void prepareProjectTimePriceOverviewDTO(Long id, Tuple userData, UserTimePriceOverviewDTO projectTimePriceOverviewDTO) {
        List<Integer> projectYears = this.getAllProjectYears(id);

        if (projectYears.size() < 1) {
            return;
        }

        BigDecimal projectTotalCost = this.getProjectTotalCostForYears(projectYears.get(0), projectYears.get(projectYears.size() - 1));
        BigDecimal costToHour = this.getUserCostToHour(projectYears.get(0), projectYears.get(projectYears.size() - 1), userData.get(QTime.time1.user.uid), projectTotalCost);

        projectTimePriceOverviewDTO.setName(StringUtils.getAppUserFullName(userData.get(QTime.time1.user.firstName), userData.get(QTime.time1.user.lastName)));
        projectTimePriceOverviewDTO.setHours(userData.get(QTime.time1.time.sum()));

        projectTimePriceOverviewDTO.setPriceNetto(userData.get(QTime.time1.price.sum()));
        projectTimePriceOverviewDTO.setHourNetto(this.countHourNetto(userData.get(QTime.time1.time.sum()), userData.get(QTime.time1.price.sum())));
        projectTimePriceOverviewDTO.setHourBrutto(projectTimePriceOverviewDTO.getHourNetto().add(costToHour));
        projectTimePriceOverviewDTO.setPriceBrutto(
                projectTimePriceOverviewDTO.getHourNetto().equals(projectTimePriceOverviewDTO.getHourBrutto()) ? projectTimePriceOverviewDTO.getPriceNetto() :
                        new BigDecimal((double) userData.get(QTime.time1.time.sum()) / 3600).setScale(2, RoundingMode.HALF_UP)
                                .multiply(projectTimePriceOverviewDTO.getHourBrutto()).setScale(2, RoundingMode.HALF_UP)
        );
    }

    @Override
    public List<Time> getProjectUsersTimesOverview(Long id, LocalDate dateFrom, LocalDate dateTo, String categoryName, String userUid) {
        Category category = categoryName.equals("_all_") ? null :
                this.categoryRepository.findCategoryByName(categoryName, SecurityUtils.companyId()).orElse(null);

        return this.qTimeRepository.getProjectAllUsersTimes(id, category, dateFrom, dateTo, userUid, SecurityUtils.companyId());
    }

    @Override
    public String getProjectTimeForThisYear(Long id) {
        Integer currentYear = LocalDate.now().getYear();

        return StringUtils.convertMinutesTimeToHoursString(this.qTimeRepository.getTotalProjectTimeForYear(id, currentYear, SecurityUtils.companyId()));
    }

    @Override
    public ProjectManHoursDTO getProjectManHours(Long id) {
        ProjectManHoursDTO projectManHoursDTO = new ProjectManHoursDTO();
        List<UserTimePriceOverviewDTO> projectTimePriceOverviewDTOList = new ArrayList<>();

        List<Tuple> userTimesPriceSums = this.qTimeRepository.getProjectUsersTimePriceSums(id, SecurityUtils.companyId());

        userTimesPriceSums.forEach(tuple -> {
            UserTimePriceOverviewDTO projectTimePriceOverviewDTO = new UserTimePriceOverviewDTO();

            prepareProjectTimePriceOverviewDTO(id, tuple, projectTimePriceOverviewDTO);

            projectManHoursDTO.setSumPriceNetto(projectManHoursDTO.getSumPriceNetto().add(tuple.get(QTime.time1.price.sum())));
            projectManHoursDTO.setSumPriceBrutto(projectManHoursDTO.getSumPriceBrutto().add(projectTimePriceOverviewDTO.getPriceBrutto()));

            projectTimePriceOverviewDTOList.add(projectTimePriceOverviewDTO);
        });

        projectManHoursDTO.setUserTimePriceOverviewList(projectTimePriceOverviewDTOList);

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

    private BigDecimal countHourNetto(Integer timeSum, BigDecimal priceSum) {
        BigDecimal minutePrice = priceSum.divide(new BigDecimal(timeSum / 60), 2, RoundingMode.HALF_UP);

        return minutePrice.multiply(new BigDecimal(60)).setScale(2, RoundingMode.HALF_UP);
    }

    private List<Integer> getAllProjectYears(Long id) {
        return this.qTimeRepository.getProjectYears(id, SecurityUtils.companyId());
    }

    private BigDecimal getProjectTotalCostForYears(Integer firstYear, Integer lastYear) {
        LocalDate firstYearStart = LocalDate.of(firstYear, Month.JANUARY, 1);
        LocalDate lastYearEnd = LocalDate.of(lastYear, Month.DECEMBER, 31);

        return this.costRepository.getProjectTotalCostBetweenYears(firstYearStart, lastYearEnd, Boolean.FALSE, Boolean.FALSE, SecurityUtils.companyId());
    }

    private double convertTimeSecondsToHours(int time) {
        return (double) time/60/60;
    }
}
