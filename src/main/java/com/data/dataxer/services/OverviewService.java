package com.data.dataxer.services;

import com.data.dataxer.models.dto.UserHourOverviewDTO;

import java.time.LocalDate;
import java.util.List;

public interface OverviewService {
    List<UserHourOverviewDTO> getAllUsersHourOverview(LocalDate fromDate, LocalDate toDate);
}