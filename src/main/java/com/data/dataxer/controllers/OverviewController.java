package com.data.dataxer.controllers;

import com.data.dataxer.models.dto.UserHourOverviewDTO;
import com.data.dataxer.models.dto.UserYearOverviewDTO;
import com.data.dataxer.services.OverviewService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/overview")
public class OverviewController {

    private final OverviewService overviewService;

    public OverviewController(OverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @GetMapping("/usersHoursOverview")
    public ResponseEntity<List<UserHourOverviewDTO>> userHourStatisticForMonth(
            @RequestParam(value = "fromDate")
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(value = "toDate")
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(this.overviewService.getAllUsersHourOverview(fromDate, toDate));
    }

    @GetMapping("/userYearsOverview")
    public ResponseEntity<List<UserYearOverviewDTO>> userYearStatistic() {
        return ResponseEntity.ok(this.overviewService.getAllUsersYearsOverview());
    }
}
