package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.dto.MonthAndYearDTO;
import com.data.dataxer.repositories.TimeRepository;
import com.data.dataxer.repositories.qrepositories.QProjectRepository;
import com.data.dataxer.repositories.qrepositories.QSalaryRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class TimeServiceImpl implements TimeService {
    private final Long LIMIT = 5L;

    private final TimeRepository timeRepository;
    private final QTimeRepository qTimeRepository;
    private final QSalaryRepository qSalaryRepository;
    private final QProjectRepository qProjectRepository;

    public TimeServiceImpl(TimeRepository timeRepository, QTimeRepository qTimeRepository, QSalaryRepository qSalaryRepository, QProjectRepository qProjectRepository) {
        this.timeRepository = timeRepository;
        this.qTimeRepository = qTimeRepository;
        this.qSalaryRepository = qSalaryRepository;
        this.qProjectRepository = qProjectRepository;
    }

    @Override
    public Time store(Time time) {
        this.addDataToTime(time);

        return this.timeRepository.save(time);
    }

    @Override
    public void update(Time time) {
        this.addDataToTime(time);

        this.timeRepository.save(time);
    }

    private void addDataToTime(Time time) {
        Salary salary = this.qSalaryRepository.getPriceFromSalaryByUserFinishIsNull(SecurityUtils.loggedUser(), SecurityUtils.companyId());

        time.setSalary(salary);
        time.setUser(SecurityUtils.loggedUser());
        time.setPrice(BigDecimal.valueOf((float) time.getTime() / 60 / 60).multiply(salary.getPrice())); // calc total price by time store
    }

    @Override
    public void destroy(Long id) {
        this.timeRepository.delete(this.getTimeByIdSimple(id));
    }

    @Override
    public Time getTimeById(Long id) {
        return this.qTimeRepository
                .getById(id, SecurityUtils.id(), SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Time not found!"));
    }

    @Override
    public Time getTimeByIdSimple(Long id) {
        return this.qTimeRepository
                .getByIdSimple(id, SecurityUtils.id(), SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Time not found"));
    }

    @Override
    public Page<Time> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qTimeRepository
                .paginate(pageable, rqlFilter, sortExpression, SecurityUtils.id(), SecurityUtils.companyId());
    }

    @Override
    public List<Time> allForPeriod(LocalDate from, LocalDate to) {
        return this.qTimeRepository.allForPeriod(from, to, SecurityUtils.id(), SecurityUtils.companyId());
    }

    @Override
    public List<Category> lastProjectCategories(Long projectId) {
        List<Category> categories = new ArrayList<>();
        List<Tuple> dataTuple = this.qTimeRepository.getProjectLastCategories(projectId, LIMIT, SecurityUtils.companyId());

        dataTuple.forEach(tuple -> categories.add(tuple.get(QTime.time1.category)));

        return categories;
    }

    @Override
    public List<MonthAndYearDTO> getAllUserMonths(Long userId) {
        List<MonthAndYearDTO> response = new ArrayList<>();
        List<Tuple> yearsAndMonths = this.qTimeRepository.getAllUserMonths(userId, SecurityUtils.companyId());

        yearsAndMonths.forEach(
                tuple -> response.add(new MonthAndYearDTO(tuple.get(QTime.time1.dateWork.year()), tuple.get(QTime.time1.dateWork.month())))
        );

        return response;
    }

    @Override
    public List<Project> getAllUserProjects(Long userId) {
        return this.qTimeRepository.getAllUserProjects(userId, SecurityUtils.companyId());
    }

    @Override
    public List<Project> getLastUserWorkingProjects(Long userId) {
        List<Project> projects = new ArrayList<>();
        List<Tuple> dataTuple = this.qTimeRepository.getUserLastProjects(SecurityUtils.id(), LIMIT, SecurityUtils.companyId());
        dataTuple.forEach(data -> projects.add(data.get(QTime.time1.project)));

        return projects;
    }
}
