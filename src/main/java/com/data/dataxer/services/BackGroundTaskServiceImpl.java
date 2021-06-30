package com.data.dataxer.services;

import com.data.dataxer.models.domain.BackGroundTask;
import com.data.dataxer.repositories.BackGroundTaskRepository;
import com.data.dataxer.tasks.UsersYearsHoursTask;
import org.springframework.stereotype.Service;

@Service
public class BackGroundTaskServiceImpl implements BackGroundTaskService {

    private final BackGroundTaskRepository backGroundTaskRepository;
    private final UsersYearsHoursTask usersYearsHoursTask;

    public BackGroundTaskServiceImpl(BackGroundTaskRepository backGroundTaskRepository, UsersYearsHoursTask usersYearsHoursTask) {
        this.backGroundTaskRepository = backGroundTaskRepository;
        this.usersYearsHoursTask = usersYearsHoursTask;
    }

    @Override
    public void scheduleUsersYearsHoursTask() {
        BackGroundTask backGroundTask = new BackGroundTask();
        backGroundTask.setName(UsersYearsHoursTask.class.getName());
        backGroundTask.setParams(null);
        backGroundTask.setLastExecution(null);
        this.backGroundTaskRepository.save(backGroundTask);

        usersYearsHoursTask.executeNow();
    }
}
