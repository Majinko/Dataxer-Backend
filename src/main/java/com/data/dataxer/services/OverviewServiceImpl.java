package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Time;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Service
public class OverviewServiceImpl implements OverviewService {

    private final QTimeRepository qTimeRepository;

    public OverviewServiceImpl(QTimeRepository qTimeRepository) {
        this.qTimeRepository = qTimeRepository;
    }

    @Override
    public HashMap<AppUser, HashMap<Integer, Integer>> getAllUsersHourOverview(LocalDate fromDate, LocalDate toDate) {
        List<Time> allUsersTimes = this.qTimeRepository.getHourOverviewForAllUsers(fromDate, toDate, SecurityUtils.companyId());

        HashMap<AppUser, HashMap<Integer, Integer>> userHourOverviewHashMap = new HashMap<>();

        for (Time time: allUsersTimes) {
            if (userHourOverviewHashMap.containsKey(time.getUser())) {
                HashMap<Integer, Integer> userHours = userHourOverviewHashMap.get(time.getUser());
                if (userHours.containsKey(time.getDateWork().getDayOfMonth())) {
                    Integer newUserDayTime = userHours.get(time.getDateWork().getDayOfMonth()) + time.getTime();
                    userHours.replace(time.getDateWork().getDayOfMonth(), newUserDayTime);
                } else {
                    userHours.put(time.getDateWork().getDayOfMonth(), time.getTime());
                }
                userHourOverviewHashMap.replace(time.getUser(), userHours);
            } else {
                HashMap<Integer, Integer> userHours = new HashMap<>();
                userHours.put(time.getDateWork().getDayOfMonth(), time.getTime());
                userHourOverviewHashMap.put(time.getUser(), userHours);
            }
        }

        return userHourOverviewHashMap;
    }
}
