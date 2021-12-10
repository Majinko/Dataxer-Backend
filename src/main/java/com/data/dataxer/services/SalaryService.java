package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Salary;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface SalaryService {
    void initUserStoreSalary(Salary salary, AppUser appUser);

    List<Salary> getUserSalaries(String uid, Sort sort);

    void store(Salary salary);

    Salary getById(Long id);

    Salary getActiveSalary(String uid);

    void update(Salary salaryDTOtoSalary);
}
