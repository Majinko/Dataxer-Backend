package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.dto.EvaluationPreparationDTO;
import com.data.dataxer.models.dto.ProjectManHoursDTO;
import com.data.dataxer.models.dto.ProjectTimePriceOverviewCategoryDTO;
import com.data.dataxer.models.dto.UserTimePriceOverviewDTO;
import com.data.dataxer.models.enums.CategoryType;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.repositories.CostRepository;
import com.data.dataxer.repositories.ProjectRepository;
import com.data.dataxer.repositories.qrepositories.QCostRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private QProjectRepository qProjectRepository;

    @Autowired
    private QTimeRepository qTimeRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private QCostRepository qCostRepository;

    @Override
    public Project store(Project project) {
        return this.projectRepository.save(project);
    }

    @Override
    public Project getById(Long id) {
        return this.qProjectRepository.getById(id, SecurityUtils.companyIds());
    }

    @Override
    public void update(Project project) {
        this.projectRepository.save(project);
    }

    @Override
    public Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return qProjectRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyIds());
    }

    @Override
    public void destroy(Long id) {
        this.projectRepository.delete(this.qProjectRepository.getById(id, SecurityUtils.companyIds()));
    }

    @Override
    public List<Project> all() {
        return this.projectRepository.findAllByCompanyIdIn(SecurityUtils.companyIds());
    }

    @Override
    public List<Project> allByClient(Long clientId) {
        return this.projectRepository.findAllByContactIdAndCompanyIdIn(clientId, SecurityUtils.companyIds());
    }

    @Override
    public List<Project> search(String queryString) {
        return this.qProjectRepository.search(SecurityUtils.companyIds(), queryString);
    }

    @Override
    public List<Category> getAllProjectCategories(Long projectId) {
        List<Category> categories = this.qProjectRepository.getById(projectId, SecurityUtils.companyIds()).getCategories();

        categories.sort(Comparator.comparing(category -> category.getPosition() != null ? category.getPosition() : 0));

        return this.qProjectRepository.getById(projectId, SecurityUtils.companyIds()).getCategories();
    }

    @Override
    public List<Category> getAllProjectCategoriesOrderedByPosition(Long projectId) {
        List<Category> categories = this.qProjectRepository.getById(projectId, SecurityUtils.companyIds()).getCategories();
        categories.sort(Comparator.comparing(Category::getPosition));

        return categories;
    }


    @Override
    public List<ProjectTimePriceOverviewCategoryDTO> getProjectCategoryOverview(Long id, Long categoryParentId) {
        List<Integer> projectYears = this.getAllProjectYears(id, null);
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

    private void prepareProjectTimePriceOverviewDTO(Tuple userData, UserTimePriceOverviewDTO projectTimePriceOverviewDTO, BigDecimal projectTotalCost, Integer firstYear, Integer lastYear) {
        BigDecimal costToHour = this.getUserCostToHour(firstYear, lastYear, userData.get(QTime.time1.user.uid), projectTotalCost);

        projectTimePriceOverviewDTO.setName(StringUtils.getAppUserFullName(userData.get(QTime.time1.user.firstName), userData.get(QTime.time1.user.lastName)));
        projectTimePriceOverviewDTO.setPhotoUrl(userData.get(QTime.time1.user.photoUrl));
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
    public ProjectManHoursDTO getProjectManHours(Long id, List<Long> companyIds) {
        ProjectManHoursDTO projectManHoursDTO = new ProjectManHoursDTO();
        List<Integer> projectYears = this.getAllProjectYears(id, companyIds);

        if (!projectYears.isEmpty()) {
            List<UserTimePriceOverviewDTO> projectTimePriceOverviewDTOList = new ArrayList<>();
            BigDecimal projectTotalCost = this.getProjectTotalCostForYears(projectYears.get(0), projectYears.get(projectYears.size() - 1)); // todo k nakladom pridat aj casy ktore suvisia s chodom firmy teda niesu ziskove
            List<Tuple> userTimesPriceSums = this.qTimeRepository.getProjectUsersTimePriceSums(id, SecurityUtils.companyIds(companyIds));

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
        return new EvaluationPreparationDTO();
    }

    @Override
    public void addProfitUser(Long id, AppUser user) {
        ObjectMapper objectMapper = new ObjectMapper();
        Project project = this.qProjectRepository.getById(id, SecurityUtils.companyIds());

        try {
            Set<String> uniqueUsers = new HashSet<>();
            if (project.getProfitUsers() != null && project.getProfitUsers() != "") {
                uniqueUsers = new HashSet<>(objectMapper.readValue(project.getProfitUsers(), new TypeReference<List<String>>() {
                }));
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
        Project project = this.qProjectRepository.getById(id, SecurityUtils.companyIds());

        try {
            if (project.getProfitUsers() != null && project.getProfitUsers() != "") {
                Set<String> uniqueUsers = new HashSet<>(objectMapper.readValue(project.getProfitUsers(), new TypeReference<List<String>>() {
                }));
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

    @Override
    public List<Project> allHasCost() {
        return this.qProjectRepository.allHasCost(SecurityUtils.companyIds());
    }

    @Override
    public List<Project> allHasInvoice() {
        return this.qProjectRepository.allHasInvoice(SecurityUtils.companyIds());
    }

    @Override
    public List<Project> allHasPriceOffer() {
        return this.qProjectRepository.allHasPriceOffer(SecurityUtils.companyIds());
    }

    @Override
    public List<Project> allHasUserTime() {
        return this.qProjectRepository.allHasUserTime(SecurityUtils.uid(), SecurityUtils.companyIds());
    }

    private BigDecimal getUserCostToHour(Integer startYear, Integer endYear, String userUid, BigDecimal projectTotalCost) {
        int userTimeBetweenYears = this.qTimeRepository.getUserProjectTimeBetweenYears(startYear, endYear, userUid, SecurityUtils.companyIds());
        int userActiveMonths = this.qTimeRepository.getUserActiveMonths(startYear, endYear, userUid, SecurityUtils.companyIds()).size();
        BigDecimal allActiveMonths = new BigDecimal(this.qTimeRepository.getProjectAllUsersActiveMonth(startYear, endYear, SecurityUtils.companyIds()).size());

        if (userActiveMonths == 0 || projectTotalCost == null) {
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

    private List<Integer> getAllProjectYears(Long id, List<Long> companyIds) {
        return this.qTimeRepository.getProjectYears(id, SecurityUtils.companyIds(companyIds));
    }

    private BigDecimal getProjectTotalCostForYears(Integer firstYear, Integer lastYear) {
        return this.getProjectTotalCostForYears(firstYear, lastYear, null);
    }

    private BigDecimal getProjectTotalCostForYears(Integer firstYear, Integer lastYear, List<Long> companyIds) { // ak by som chcel naklad k hodinovke // todo nezabudnut k hodinovke aj mzdy
        LocalDate firstYearStart = LocalDate.of(firstYear, Month.JANUARY, 1);
        LocalDate lastYearEnd = LocalDate.of(lastYear, Month.DECEMBER, 31);

        return this.qCostRepository.getProjectTotalCostBetweenYears(firstYearStart, lastYearEnd, Boolean.FALSE, Boolean.FALSE, this.getCategoriesIdInProjectCost(), SecurityUtils.companyIds(companyIds));
    }

    /**
     * Get categories ids
     */
    private List<Long> getCategoriesIdInProjectCost() {
        return this.categoryRepository.findAllIdsCategoryTypeInAndCompanyIdIn(CategoryType.getManHoursTypes(), SecurityUtils.companyIds());
    }

    private double convertTimeSecondsToHours(int time) {
        return (double) time / 60 / 60;
    }
}
