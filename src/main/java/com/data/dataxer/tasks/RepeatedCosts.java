package com.data.dataxer.tasks;

import com.data.dataxer.services.CostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RepeatedCosts {

    @Autowired
    private CostService costService;

    //@Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 * * * * *")
    void execute() {
        costService.taskExecute();
    }
}
