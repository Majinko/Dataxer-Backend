package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.dto.*;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.repositories.CostRepository;
import com.data.dataxer.repositories.ProjectRepository;
import com.data.dataxer.repositories.qrepositories.QInvoiceRepository;
import com.data.dataxer.repositories.qrepositories.QProjectRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.StringUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final QProjectRepository qProjectRepository;
    private final QTimeRepository qTimeRepository;
    private final CategoryRepository categoryRepository;
    private final CostRepository costRepository;
    private final QInvoiceRepository qInvoiceRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, QProjectRepository qProjectRepository,
                              QTimeRepository qTimeRepository, CategoryRepository categoryRepository,
                              CostRepository costRepository,
                              QInvoiceRepository qInvoiceRepository) {
        this.projectRepository = projectRepository;
        this.qProjectRepository = qProjectRepository;
        this.qTimeRepository = qTimeRepository;
        this.categoryRepository = categoryRepository;
        this.costRepository = costRepository;
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
        categories.sort(Comparator.comparing(Category::getPosition));

        return categories;
    }


    @Override
    public List<ProjectTimePriceOverviewCategoryDTO> getProjectCategoryOverview(Long id, Long categoryParentId) {
        List<Integer> projectYears = this.getAllProjectYears(id);
        List<ProjectTimePriceOverviewCategoryDTO> response = new ArrayList<>();

        if (!projectYears.isEmpty()) {
            List<Category> categoriesForReport = this.prepareParentCategories(categoryParentId);
            BigDecimal projectTotalCost = this.getProjectTotalCostForYears(projectYears.get(0), projectYears.get(projectYears.size() - 1));

            categoriesForReport.forEach(category -> {
                List<UserTimePriceOverviewDTO> responseValue = new ArrayList<>();
                List<Tuple> categoryUsersData = this.qTimeRepository.getAllProjectUserCategoryData(id, this.categoryRepository.findAllChildIdsHasTime(category.getId(), SecurityUtils.companyId()), SecurityUtils.companyId());

                categoryUsersData.forEach(tuple -> {
                    UserTimePriceOverviewDTO projectTimePriceOverviewDTO = new UserTimePriceOverviewDTO();

                    prepareProjectTimePriceOverviewDTO(tuple, projectTimePriceOverviewDTO, projectTotalCost, projectYears.get(0), projectYears.get(projectYears.size() - 1));

                    responseValue.add(projectTimePriceOverviewDTO);
                });

                if (responseValue.size() > 0) {
                    response.add(new ProjectTimePriceOverviewCategoryDTO(category.getId(), category.getName(), responseValue.stream().mapToInt(UserTimePriceOverviewDTO::getHours).sum(), responseValue));
                }
            });
        }


        return response;
    }

    private List<Category> prepareParentCategories(Long categoryParentId) {
        return categoryParentId == null
                ? this.categoryRepository.findByParentIdIsNullAndCompanyId(SecurityUtils.companyId()).orElse(new ArrayList<>())
                : this.categoryRepository.findAllByParentIdAndCompanyId(categoryParentId, SecurityUtils.companyId());
    }

    private void prepareProjectTimePriceOverviewDTO(Tuple userData, UserTimePriceOverviewDTO projectTimePriceOverviewDTO, BigDecimal projectTotalCost,
                                                    Integer firstYear, Integer lastYear) {
        BigDecimal costToHour = this.getUserCostToHour(firstYear, lastYear, userData.get(QTime.time1.user.uid), projectTotalCost);

        projectTimePriceOverviewDTO.setName(StringUtils.getAppUserFullName(userData.get(QTime.time1.user.firstName), userData.get(QTime.time1.user.lastName)));
        projectTimePriceOverviewDTO.setHours(userData.get(QTime.time1.time.sum()));

        projectTimePriceOverviewDTO.setPriceNetto(userData.get(QTime.time1.price.sum()));
        projectTimePriceOverviewDTO.setHourNetto(this.countHourNetto(userData.get(QTime.time1.time.sum()), Objects.requireNonNull(userData.get(QTime.time1.price.sum()))));
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
        List<Integer> projectYears = this.getAllProjectYears(id);

        if (!projectYears.isEmpty()) {
            List<UserTimePriceOverviewDTO> projectTimePriceOverviewDTOList = new ArrayList<>();
            BigDecimal projectTotalCost = this.getProjectTotalCostForYears(projectYears.get(0), projectYears.get(projectYears.size() - 1));
            List<Tuple> userTimesPriceSums = this.qTimeRepository.getProjectUsersTimePriceSums(id, SecurityUtils.companyId());

            userTimesPriceSums.forEach(tuple -> {
                UserTimePriceOverviewDTO projectTimePriceOverviewDTO = new UserTimePriceOverviewDTO();

                prepareProjectTimePriceOverviewDTO(tuple, projectTimePriceOverviewDTO, projectTotalCost, projectYears.get(0), projectYears.get(projectYears.size() - 1));

                projectManHoursDTO.setSumPriceNetto(projectManHoursDTO.getSumPriceNetto().add(tuple.get(QTime.time1.price.sum())));
                projectManHoursDTO.setSumPriceBrutto(projectManHoursDTO.getSumPriceBrutto().add(projectTimePriceOverviewDTO.getPriceBrutto()));

                projectTimePriceOverviewDTOList.add(projectTimePriceOverviewDTO);
            });

            Collections.sort(projectTimePriceOverviewDTOList);

            projectManHoursDTO.setUserTimePriceOverviewList(projectTimePriceOverviewDTOList);
        }

        return projectManHoursDTO;
    }

    @Override
    public EvaluationPreparationDTO evaluationPreparationProjectData(Long id) {
        EvaluationPreparationDTO response = new EvaluationPreparationDTO();

        List<Cost> costs = this.costRepository.findAllByProjectIdAndCompanyId(id, SecurityUtils.companyId());
        List<Invoice> invoices = this.qInvoiceRepository.getAllProjectInvoices(id, SecurityUtils.companyId());
        List<Time> times = this.qTimeRepository.getAllProjectTimesOrdered(id, SecurityUtils.companyId());

        return response;
    }

    @Override
    public void addProfitUser(Long id, AppUser user) {
        ObjectMapper objectMapper = new ObjectMapper();
        Project project = this.qProjectRepository.getById(id, SecurityUtils.companyId());

        try {
            Set<String> uniqueUsers = new HashSet<>();
            if (project.getProfitUsers() != null && project.getProfitUsers() != "") {
                uniqueUsers = new HashSet<>(objectMapper.readValue(project.getProfitUsers(), new TypeReference<List<String>>(){}));
            }
            uniqueUsers.add(user.getUid());
            project.setProfitUsers(objectMapper.writeValueAsString(uniqueUsers));
            this.projectRepository.save(project);
        } catch (JsonMappingException e) {
        } catch (JsonParseException e) {
        } catch (IOException e) {
            throw new RuntimeException("JSON parsing failed");
        }
    }

    @Override
    public void removeProfitUser(Long id, AppUser user) {
        ObjectMapper objectMapper = new ObjectMapper();
        Project project = this.qProjectRepository.getById(id, SecurityUtils.companyId());

        try {
            if (project.getProfitUsers() != null && project.getProfitUsers() != "") {
                Set<String> uniqueUsers = new HashSet<>(objectMapper.readValue(project.getProfitUsers(), new TypeReference<List<String>>(){}));
                uniqueUsers.remove(user.getUid());
                project.setProfitUsers(objectMapper.writeValueAsString(uniqueUsers));
                this.projectRepository.save(project);
            }
        } catch (JsonMappingException e) {
        } catch (JsonParseException e) {
        } catch (IOException e) {
            throw new RuntimeException("JSON parsing failed");
        }
    }

    private ProjectStatisticDTO prepareProjectStatisticDTO(Long id) {
        ProjectStatisticDTO projectStatisticDTO = new ProjectStatisticDTO();
        List<Integer> projectYears = this.getAllProjectYears(id);

        if (!projectYears.isEmpty()) {
            BigDecimal projectTotalCost = this.getProjectTotalCostForYears(projectYears.get(0), projectYears.get(projectYears.size() - 1));

            List<Time> projectOrderedTimes = this.qTimeRepository.getAllProjectTimesOrdered(id, SecurityUtils.companyId());
            List<Tuple> userTimesPriceSums = this.qTimeRepository.getProjectUsersTimePriceSums(id, SecurityUtils.companyId());

            int totalProjectTime = 0;
            BigDecimal projectHourNettoSum = BigDecimal.ZERO;
            BigDecimal projectHourBruttoSum = BigDecimal.ZERO;

            for (Tuple data : userTimesPriceSums) {
                UserTimePriceOverviewDTO userData = new UserTimePriceOverviewDTO();
                prepareProjectTimePriceOverviewDTO(data, userData, projectTotalCost, projectYears.get(0), projectYears.get(projectYears.size() - 1));

                totalProjectTime += userData.getHours();
                projectHourNettoSum = projectHourNettoSum.add(userData.getHourNetto());
                projectHourBruttoSum = projectHourBruttoSum.add(userData.getHourBrutto());
            }

            projectStatisticDTO.setProjectStart(projectOrderedTimes.get(0).getDateWork());
            projectStatisticDTO.setProjectEnd(projectOrderedTimes.get(projectOrderedTimes.size() - 1).getDateWork());
            projectStatisticDTO.setProjectTakeInMonths(Period.between(projectStatisticDTO.getProjectStart(), projectStatisticDTO.getProjectEnd()).getMonths());
            projectStatisticDTO.setProjectManHours(StringUtils.convertMinutesTimeToHoursString(totalProjectTime));
            projectStatisticDTO.setAverageManHoursForMonth(StringUtils.convertMinutesTimeToHoursString(totalProjectTime / userTimesPriceSums.size()));
            projectStatisticDTO.setAverageHourNetto(projectHourNettoSum.divide(new BigDecimal(convertTimeSecondsToHours(totalProjectTime)), 2, RoundingMode.HALF_UP));
            projectStatisticDTO.setAverageHourBrutto(projectHourBruttoSum.divide(new BigDecimal(convertTimeSecondsToHours(totalProjectTime)), 2, RoundingMode.HALF_UP));
            //projectStatisticDTO.setProjectPayedPrice(projectPrice);
            projectStatisticDTO.setProjectPayedCosts(projectTotalCost);
            projectStatisticDTO.setProjectPayedInternalCosts(projectHourNettoSum);
            projectStatisticDTO.setProjectHourBruttoSum(projectHourBruttoSum);
            projectStatisticDTO.setProjectRunningCosts(projectHourBruttoSum.subtract(projectHourNettoSum));
        }

        return projectStatisticDTO;
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
        return (double) time / 60 / 60;
    }
}
