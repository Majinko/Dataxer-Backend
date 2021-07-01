package com.data.dataxer.controllers;

import com.data.dataxer.services.BackGroundTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/taskPlanner")
public class BackGroundTaskController {

    private final BackGroundTaskService backGroundTaskService;

    public BackGroundTaskController(BackGroundTaskService backGroundTaskService) {
        this.backGroundTaskService = backGroundTaskService;
    }

    @GetMapping("/scheduleUYH")
    public void scheduleUYH() {
        this.backGroundTaskService.scheduleUsersYearsHoursTask();
    }

}
