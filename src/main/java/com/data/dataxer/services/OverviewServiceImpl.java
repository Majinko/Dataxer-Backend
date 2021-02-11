package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Salary;
import com.data.dataxer.models.domain.Time;
import com.data.dataxer.models.dto.UserHourOverviewDTO;
import com.data.dataxer.repositories.qrepositories.QSalaryRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OverviewServiceImpl implements OverviewService {

    private final QTimeRepository qTimeRepository;
    private final QSalaryRepository qSalaryRepository;

    private HashMap<AppUser, HashMap<Integer, Integer>> userDayHoursInMinutes;
    private HashMap<AppUser, HashMap<Integer, BigDecimal>> userDayTotalPrice;

    public OverviewServiceImpl(QTimeRepository qTimeRepository, QSalaryRepository qSalaryRepository) {
        this.qTimeRepository = qTimeRepository;
        this.qSalaryRepository =qSalaryRepository;
    }

    @Override
    public List<UserHourOverviewDTO> getAllUsersHourOverview(LocalDate fromDate, LocalDate toDate) {
        List<Time> allUsersTimes = this.qTimeRepository.getHourOverviewForAllUsers(fromDate, toDate, SecurityUtils.companyId());

        this.userDayHoursInMinutes = new HashMap<>();
        this.userDayTotalPrice = new HashMap<>();

        for (Time time: allUsersTimes) {
            //ak uz user data ma => staci z jednej hashmapy, user je v oboch alebo v ziadnej
            if (userDayHoursInMinutes.containsKey(time.getUser())) {
                //ak uz dany den v mesiaci ma zaznam
                if (userDayHoursInMinutes.get(time.getUser()).containsKey(time.getDateWork().getDayOfMonth())) {
                    Integer newUserDayTime = userDayHoursInMinutes.get(time.getUser()).get(time.getDateWork().getDayOfMonth()) + time.getTime();
                    userDayHoursInMinutes.get(time.getUser()).replace(time.getDateWork().getDayOfMonth(), newUserDayTime);

                    BigDecimal newUserDayTotalPrice = userDayTotalPrice.get(time.getUser()).get(time.getDateWork().getDayOfMonth())
                            .add(new BigDecimal(time.getTime() / 60 ).multiply(time.getPrice()));
                    userDayTotalPrice.get(time.getUser()).replace(time.getDateWork().getDayOfMonth(), newUserDayTotalPrice);
                } else {
                    userDayHoursInMinutes.get(time.getUser()).put(time.getDateWork().getDayOfMonth(), time.getTime());
                    userDayTotalPrice.get(time.getUser()).put(time.getDateWork().getDayOfMonth(), new BigDecimal(time.getTime() / 60 ).multiply(time.getPrice()));
                }
            } else {
                //user este ziadne data nema => staci z jednej hashmapy, user je v oboch alebo v ziadnej
                HashMap<Integer, Integer> userHours = new HashMap<>();
                HashMap<Integer, BigDecimal> userHourDayPrice = new HashMap<>();

                //zoberie pocet odpracovanych minut
                userHours.put(time.getDateWork().getDayOfMonth(), time.getTime());
                //vypocita celkovu cenu prace za dany cas v dany den => pouziva prepocet minut do desiatkovejsustavy
                userHourDayPrice.put(time.getDateWork().getDayOfMonth(), new BigDecimal(time.getTime() / 60 ).multiply(time.getPrice()));

                userDayHoursInMinutes.put(time.getUser(), userHours);
                userDayTotalPrice.put(time.getUser(), userHourDayPrice);
            }
        }

        return this.fillUsersOverviewData();
    }

    private List<UserHourOverviewDTO> fillUsersOverviewData() {
        List<UserHourOverviewDTO> filedResponse = new ArrayList<>();

        HashMap<Long, Salary> userSalaryHashMap = this.getAllUsersSalaries(
                userDayHoursInMinutes.keySet().stream().map(AppUser::getId).collect(Collectors.toList())
        );

        System.out.println("Size: " + userSalaryHashMap.size());

        userDayHoursInMinutes.keySet().iterator().forEachRemaining(key -> {
            UserHourOverviewDTO userHourOverviewDTO = new UserHourOverviewDTO();
            userHourOverviewDTO.setFirstName(key.getFirstName());
            userHourOverviewDTO.setLastName(key.getLastName());
            userHourOverviewDTO.setUserTimePrices(userDayTotalPrice.get(key));
            userHourOverviewDTO.setSalaryType(userSalaryHashMap.get(key.getId()).getSalaryType());
            userHourOverviewDTO.setActiveHourPrice(userSalaryHashMap.get(key.getId()).getPrice());
            userHourOverviewDTO.setUserHours(this.generateUserHoursStringFromMinutes(userDayHoursInMinutes.get(key)));
            userHourOverviewDTO.setTotalUserPrice(this.countUserTotalPrice(userDayTotalPrice.get(key)));
            userHourOverviewDTO.setTotalUserHours(this.countTotalUserHours(userDayHoursInMinutes.get(key)));
            filedResponse.add(userHourOverviewDTO);
        });

        return filedResponse;
    }

    private HashMap<Long, Salary> getAllUsersSalaries(List<Long> userIds) {
        HashMap<Long, Salary> userSalaryHashMap = new HashMap<>();

        //load just needed salaries
        List<Salary> userSalaries = this.qSalaryRepository.getSalariesForUsersByIds(userIds, SecurityUtils.companyId());
        System.out.println("List size: " + userSalaries.size());
        for (Salary salary: userSalaries) {
            userSalaryHashMap.put(salary.getUser().getId(), salary);
        }

        return userSalaryHashMap;
    }

    private String countTotalUserHours(HashMap<Integer, Integer> usersHourInMinutes) {
        Integer totalMinutes = 0;
        for (Integer dayMinutes: usersHourInMinutes.values()) {
            totalMinutes += dayMinutes;
        }
        return this.convertMinutesTimeToHoursString(totalMinutes);
    }

    private BigDecimal countUserTotalPrice(HashMap<Integer, BigDecimal> userPrices) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (BigDecimal price: userPrices.values()) {
            totalPrice = totalPrice.add(price);
        }
        return totalPrice;
    }

    private HashMap<Integer, String> generateUserHoursStringFromMinutes(HashMap<Integer, Integer> userHoursInMinutes) {
        HashMap<Integer, String> generatedTimes = new HashMap<>();

        userHoursInMinutes.keySet().iterator().forEachRemaining(key -> {
            generatedTimes.put(key, this.convertMinutesTimeToHoursString(userHoursInMinutes.get(key)));
        });

        return generatedTimes;
    }

    private String convertMinutesTimeToHoursString(Integer minutes) {
        return minutes / 60 + ":" + minutes % 60 + " /h";
    }

    private List<Long> getUserIds(List<AppUser> users) {
        List<Long> userIds = new ArrayList<>();
        for (AppUser user:users) {
            userIds.add(user.getId());
        }
        return userIds;
    }

}
