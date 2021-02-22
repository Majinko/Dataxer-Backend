package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Salary;

public interface SalaryService {
    void initUserStoreSalary(Salary salary, AppUser appUser);
}
