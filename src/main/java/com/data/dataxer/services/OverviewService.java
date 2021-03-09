package com.data.dataxer.services;

import com.data.dataxer.models.dto.UserHourOverviewDTO;
import com.data.dataxer.models.dto.UserYearOverviewDTO;
import com.data.dataxer.models.dto.CategoryCostsOverviewDTO;

import java.time.LocalDate;
import java.util.List;

public interface OverviewService {
    List<UserHourOverviewDTO> getAllUsersHourOverview(LocalDate fromDate, LocalDate toDate);

    List<UserYearOverviewDTO> getAllUsersYearsOverview();

    CategoryCostsOverviewDTO getCategoriesCostsForYear(Integer year, Long categoryId);

}
