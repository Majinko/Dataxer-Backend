package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Salary;

import java.util.List;

public interface QSalaryRepository {
    Salary getActiveSalary(AppUser user, Long companyId);

    List<Salary> getSalariesForUsersByIds(List<Long> userIds, Long companyId);
}
