package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Salary;
import com.data.dataxer.models.domain.Time;
import com.data.dataxer.repositories.SalaryRepository;
import com.data.dataxer.repositories.TimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalaryServiceImpl implements SalaryService {
    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private TimeRepository timeRepository;

    @Override
    public void initUserStoreSalary(Salary salary, AppUser appUser) {
        salary.setUser(appUser);
        salary.setIsActive(true);
        salary.setStart(LocalDate.now());
        salary.setFinish(null);

        salaryRepository.save(salary);
    }

    @Override
    public List<Salary> getUserSalaries(String uid, Sort sort) {
        return this.salaryRepository.findAllByUserUidAndAppProfileId(uid, sort, SecurityUtils.defaultProfileId());
    }

    @Override
    public void store(Salary salary) {
        Salary lastUserSalary = this.salaryRepository.findByUserUidAndFinishIsNullAndAppProfileId(salary.getUser().getUid(), SecurityUtils.defaultProfileId());

        if (lastUserSalary != null) {
            if (salary.getStart().isBefore(lastUserSalary.getStart())) {
                throw new RuntimeException("Start date must be less that " + lastUserSalary.getStart().toString());
            }

            lastUserSalary.setFinish(salary.getStart());

            salaryRepository.save(lastUserSalary);
        }

        salaryRepository.save(salary);
    }

    @Override
    public Salary getById(Long id) {
        return this.salaryRepository.findByIdAndAppProfileId(id, SecurityUtils.defaultProfileId()).orElseThrow(() -> new RuntimeException("Salary not found :("));
    }

    @Override
    public Salary getActiveSalary(String uid) {
        return this.salaryRepository.findByUserUidAndFinishIsNullAndAppProfileId(uid, SecurityUtils.defaultProfileId());
    }

    @Override
    // todo finish later
    public void update(Salary salary) {
        List<Time> times = this.timeRepository.findAllBySalaryIdAndAndAndAppProfileId(salary.getId(), SecurityUtils.defaultProfileId());

        //todo update and time price
        salaryRepository.findByIdAndAppProfileId(salary.getId(), SecurityUtils.defaultProfileId()).map(s -> {
            s.setPrice(salary.getPrice());
            s.setIsActive(salary.getIsActive());

            return salaryRepository.save(s);
        });
    }
}
