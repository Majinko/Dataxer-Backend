package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Salary;
import com.data.dataxer.repositories.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SalaryServiceImpl implements SalaryService {
    @Autowired
    private SalaryRepository salaryRepository;

    @Override
    public void initUserStoreSalary(Salary salary, AppUser appUser) {
        salary.setUser(appUser);
        salary.setIsActive(true);
        salary.setStart(LocalDate.now());
        salary.setFinish(null);

        salaryRepository.save(salary);
    }
}
