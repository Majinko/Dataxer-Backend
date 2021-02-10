package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;

import java.time.LocalDate;
import java.util.HashMap;

public interface OverviewService {

    HashMap<AppUser, HashMap<Integer, Integer>> getAllUsersHourOverview(LocalDate fromDate, LocalDate toDate);

}
