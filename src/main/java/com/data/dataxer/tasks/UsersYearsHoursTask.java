package com.data.dataxer.tasks;

import com.data.dataxer.models.domain.BackGroundTask;
import com.data.dataxer.repositories.BackGroundTaskRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.services.OverviewService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UsersYearsHoursTask {

    private final OverviewService overviewService;
    private final BackGroundTaskRepository backGroundTaskRepository;

    public UsersYearsHoursTask(OverviewService overviewService, BackGroundTaskRepository backGroundTaskRepository) {
        this.overviewService = overviewService;
        this.backGroundTaskRepository = backGroundTaskRepository;
    }

    @Scheduled(cron = "0 0 1 1 * *")
    void execute() {
        BackGroundTask task = this.backGroundTaskRepository.findBackGroundTaskByNameAndCompanyId(UsersYearsHoursTask.class.getName(), SecurityUtils.companyId());
        task.setParams(this.overviewService.executeUsersYearsHours(task.getParams()));
        task.setLastExecution(LocalDate.now());
        this.backGroundTaskRepository.save(task);
    }

    public void executeNow() {
        execute();
    }

}
