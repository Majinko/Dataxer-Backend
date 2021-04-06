package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.dto.CategoryCostsOverviewDTO;
import com.data.dataxer.models.dto.CategoryMonthsCostsDTO;
import com.data.dataxer.models.dto.UserHourOverviewDTO;
import com.data.dataxer.models.dto.UserYearOverviewDTO;
import com.data.dataxer.models.enums.SalaryType;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.repositories.qrepositories.QCostRepository;
import com.data.dataxer.repositories.qrepositories.QSalaryRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OverviewServiceImpl implements OverviewService {

    private final QTimeRepository qTimeRepository;
    private final QSalaryRepository qSalaryRepository;
    private final QCostRepository qCostRepository;
    private final CategoryRepository categoryRepository;

    private HashMap<AppUser, HashMap<Integer, Integer>> userTimeData;
    private HashMap<AppUser, HashMap<Integer, BigDecimal>> userDayTotalPrice;


    public OverviewServiceImpl(QTimeRepository qTimeRepository, QSalaryRepository qSalaryRepository,
                               QCostRepository qCostRepository, CategoryRepository categoryRepository) {
        this.qTimeRepository = qTimeRepository;
        this.qSalaryRepository = qSalaryRepository;
        this.qCostRepository = qCostRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<UserHourOverviewDTO> getAllUsersHourOverview(LocalDate fromDate, LocalDate toDate) {
        List<Time> allUsersTimes = this.qTimeRepository.getHourOverviewForAllUsers(fromDate, toDate, SecurityUtils.companyId());

        this.userTimeData = new HashMap<>();
        this.userDayTotalPrice = new HashMap<>();

        for (Time time : allUsersTimes) {
            //ak uz user data ma => staci z jednej hashmapy, user je v oboch alebo v ziadnej
            if (userTimeData.containsKey(time.getUser())) {
                //ak uz dany den v mesiaci ma zaznam
                if (userTimeData.get(time.getUser()).containsKey(time.getDateWork().getDayOfMonth())) {
                    Integer newUserDayTime = userTimeData.get(time.getUser()).get(time.getDateWork().getDayOfMonth()) + time.getTime();
                    userTimeData.get(time.getUser()).replace(time.getDateWork().getDayOfMonth(), newUserDayTime);

                    BigDecimal newUserDayTotalPrice = userDayTotalPrice.get(time.getUser()).get(time.getDateWork().getDayOfMonth()).add(time.getPrice());
                    userDayTotalPrice.get(time.getUser()).replace(time.getDateWork().getDayOfMonth(), newUserDayTotalPrice);
                } else {
                    userTimeData.get(time.getUser()).put(time.getDateWork().getDayOfMonth(), time.getTime());
                    userDayTotalPrice.get(time.getUser()).put(time.getDateWork().getDayOfMonth(), time.getPrice());
                }
            } else {
                //user este ziadne data nema => staci z jednej hashmapy, user je v oboch alebo v ziadnej
                HashMap<Integer, Integer> userHours = new HashMap<>();
                HashMap<Integer, BigDecimal> userHourDayPrice = new HashMap<>();

                //zoberie pocet odpracovanych minut
                userHours.put(time.getDateWork().getDayOfMonth(), time.getTime());
                //vypocita celkovu cenu prace za dany cas v dany den => pouziva prepocet minut do desiatkovejsustavy
                userHourDayPrice.put(time.getDateWork().getDayOfMonth(), time.getPrice());

                userTimeData.put(time.getUser(), userHours);
                userDayTotalPrice.put(time.getUser(), userHourDayPrice);
            }
        }

        return this.fillUsersOverviewData();
    }

    @Override
    public List<UserYearOverviewDTO> getAllUsersYearsOverview() {
        this.userTimeData = new HashMap<>();
        List<Time> allTimeRecords = this.qTimeRepository.getAllTimeRecords(SecurityUtils.companyId());

        allTimeRecords.forEach(time -> {
            if (userTimeData.containsKey(time.getUser())) {
                HashMap<Integer, Integer> yearsHour = userTimeData.get(time.getUser());
                if (yearsHour.containsKey(time.getDateWork().getYear())) {
                    Integer newHour = yearsHour.get(time.getDateWork().getYear()) + time.getTime();
                    yearsHour.replace(time.getDateWork().getYear(), newHour);
                } else {
                    yearsHour.put(time.getDateWork().getYear(), time.getTime());
                }
                userTimeData.replace(time.getUser(), yearsHour);
            } else {
                HashMap<Integer, Integer> yearsHours = new HashMap<>();
                yearsHours.put(time.getDateWork().getYear(), time.getTime());
                userTimeData.put(time.getUser(), yearsHours);
            }
        });

        return this.fillAllUsersYearsOverviewData();
    }

    @Override
    public CategoryCostsOverviewDTO getCategoriesCostsForYear(Integer year, Long categoryId) {
        CategoryCostsOverviewDTO response = new CategoryCostsOverviewDTO();

        if (categoryId == null) {
            List<Category> rootCategories = this.categoryRepository.findAllByCompanyAndParentIsNull(SecurityUtils.companyId()).orElse(new ArrayList<>());

            response.setCategoryMonthsCostsDTOS(this.generateCategoryMonthCosts(rootCategories, year));
        } else {
            response.setCategoryMonthsCostsDTOS(this.generateCategoryMonthCosts(List.of(this.categoryRepository.findCategoryByIdAndCompanyId(categoryId, SecurityUtils.companyId()).orElseThrow(() -> new RuntimeException("Category with id " + categoryId + " not found"))), year));
        }
        response.setMonthsTotalCosts(this.generateAllMonthsTotalCostHeader(response.getCategoryMonthsCostsDTOS()));
        response.setTotalCosts(this.countTotalPrice(response.getMonthsTotalCosts().values()));

        return response;
    }

    private HashMap<Integer, BigDecimal> generateAllMonthsTotalCostHeader(List<CategoryMonthsCostsDTO> categoryMonthsCostsDTOS) {
        HashMap<Integer, BigDecimal> allMonthsCosts = new HashMap<>();
        categoryMonthsCostsDTOS.forEach(categoryMonthsCostsDTO -> {
            //pre kazdu kategoriu prejst celu hashmapu => prechadzame vsetky kluce
            HashMap<Integer, BigDecimal> categoryMonthsCosts = categoryMonthsCostsDTO.getTotalMonthsCosts();
            categoryMonthsCosts.keySet().stream().iterator().forEachRemaining(key -> {
                if (allMonthsCosts.containsKey(key)) {
                    BigDecimal tmp = allMonthsCosts.get(key).add(categoryMonthsCosts.get(key));
                    allMonthsCosts.replace(key, tmp);
                } else {
                    allMonthsCosts.put(key, categoryMonthsCosts.get(key));
                }
            });
        });
        return allMonthsCosts;
    }

    private HashMap<Integer, BigDecimal> generateMonthsTotalPrice(List<Cost> costList) {
        HashMap<Integer, BigDecimal> totalMonthsCosts = new HashMap<>();
        costList.forEach(cost -> {
            if (totalMonthsCosts.containsKey(cost.getDeliveredDate().getMonthValue())) {
                BigDecimal tmpTotalPrice = totalMonthsCosts.get(cost.getDeliveredDate().getMonthValue()).add(cost.getTotalPrice());
                totalMonthsCosts.replace(cost.getDeliveredDate().getMonthValue(), tmpTotalPrice);
            } else {
                totalMonthsCosts.put(cost.getDeliveredDate().getMonthValue(), cost.getTotalPrice());
            }
        });

        return totalMonthsCosts;
    }

    private BigDecimal countTotalPrice(Collection<BigDecimal> prices) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (BigDecimal price : prices) {
            totalPrice = totalPrice.add(price);
        }
        return totalPrice;
    }

    private List<CategoryMonthsCostsDTO> generateCategoryMonthCosts(List<Category> categories, Integer year) {
        List<CategoryMonthsCostsDTO> categoryMonthsCostsDTOS = new ArrayList<>();
        categories.forEach(category -> {
            List<Long> childrenIds = this.categoryRepository.findSubTreeIds(category.getId(), SecurityUtils.companyId());

            List<Cost> costList = this.qCostRepository.getCostsWhereCategoryIdIn(childrenIds, year, SecurityUtils.companyId());

            CategoryMonthsCostsDTO categoryMonthsCostsDTO = new CategoryMonthsCostsDTO();
            categoryMonthsCostsDTO.setCategoryName(category.getName());
            categoryMonthsCostsDTO.setTotalMonthsCosts(this.generateMonthsTotalPrice(costList));
            categoryMonthsCostsDTO.setCategoryTotalPrice(this.countTotalPrice(categoryMonthsCostsDTO.getTotalMonthsCosts().values()));
            categoryMonthsCostsDTOS.add(categoryMonthsCostsDTO);
        });

        return categoryMonthsCostsDTOS;
    }

    private List<UserYearOverviewDTO> fillAllUsersYearsOverviewData() {
        List<UserYearOverviewDTO> response = new ArrayList<>();

        userTimeData.keySet().iterator().forEachRemaining(key -> {
            if (key != null) {
                UserYearOverviewDTO userYearOverviewDTO = new UserYearOverviewDTO();
                userYearOverviewDTO.setFirstName(key.getFirstName());
                userYearOverviewDTO.setLastName(key.getLastName());
                userYearOverviewDTO.setFullName(key.getFirstName() + " " + key.getLastName());
                userYearOverviewDTO.setYearHours(this.generateUserHoursStringFromMinutes(userTimeData.get(key)));
                response.add(userYearOverviewDTO);
            }
        });

        return response;
    }

    private List<UserHourOverviewDTO> fillUsersOverviewData() {
        List<UserHourOverviewDTO> filedResponse = new ArrayList<>();

        HashMap<Long, Salary> userSalaryHashMap = this.getAllUsersSalaries(
                userTimeData.keySet().stream().map(AppUser::getId).collect(Collectors.toList())
        );

        userTimeData.keySet().iterator().forEachRemaining(key -> {
            if (userSalaryHashMap.get(key.getId()) != null) {
                UserHourOverviewDTO userHourOverviewDTO = new UserHourOverviewDTO();
                userHourOverviewDTO.setFirstName(key.getFirstName());
                userHourOverviewDTO.setLastName(key.getLastName());
                userHourOverviewDTO.setFullName(key.getFirstName() + " " + key.getLastName());
                userHourOverviewDTO.setUserTimePrices(userDayTotalPrice.get(key));
                userHourOverviewDTO.setSalaryType(userSalaryHashMap.get(key.getId()).getSalaryType());
                userHourOverviewDTO.setActiveHourPrice(userSalaryHashMap.get(key.getId()).getPrice());
                userHourOverviewDTO.setUserHours(this.generateUserHoursStringFromMinutes(userTimeData.get(key)));
                if (userSalaryHashMap.get(key.getId()).getSalaryType() == SalaryType.HOUR) {
                    userHourOverviewDTO.setTotalUserPrice(this.countUserTotalPrice(userDayTotalPrice.get(key)));
                } else {
                    userHourOverviewDTO.setTotalUserPrice(userSalaryHashMap.get(key.getId()).getPrice());
                }
                userHourOverviewDTO.setTotalUserHours(this.countTotalUserHours(userTimeData.get(key)));
                filedResponse.add(userHourOverviewDTO);
            }
        });

        return filedResponse;
    }

    private HashMap<Long, Salary> getAllUsersSalaries(List<Long> userIds) {
        HashMap<Long, Salary> userSalaryHashMap = new HashMap<>();

        //load just needed salaries
        List<Salary> userSalaries = this.qSalaryRepository.getSalariesForUsersByIds(userIds, SecurityUtils.companyId());

        for (Salary salary : userSalaries) {
            userSalaryHashMap.put(salary.getUser().getId(), salary);
        }

        return userSalaryHashMap;
    }

    private String countTotalUserHours(HashMap<Integer, Integer> usersHourInMinutes) {
        Integer totalMinutes = 0;
        for (Integer dayMinutes : usersHourInMinutes.values()) {
            totalMinutes += dayMinutes;
        }
        return StringUtils.convertMinutesTimeToHoursString(totalMinutes);
    }

    private BigDecimal countUserTotalPrice(HashMap<Integer, BigDecimal> userPrices) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (BigDecimal price : userPrices.values()) {
            totalPrice = totalPrice.add(price);
        }
        return totalPrice;
    }

    private HashMap<Integer, String> generateUserHoursStringFromMinutes(HashMap<Integer, Integer> userHoursInMinutes) {
        HashMap<Integer, String> generatedTimes = new HashMap<>();

        userHoursInMinutes.keySet().iterator().forEachRemaining(key -> {
            generatedTimes.put(key, StringUtils.convertMinutesTimeToHoursString(userHoursInMinutes.get(key)));
        });

        return generatedTimes;
    }

    private List<Long> getUserIds(List<AppUser> users) {
        List<Long> userIds = new ArrayList<>();
        for (AppUser user : users) {
            userIds.add(user.getId());
        }
        return userIds;
    }
}
