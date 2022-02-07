package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.dto.MonthAndYearDTO;
import com.data.dataxer.models.enums.SalaryType;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.repositories.TimeRepository;
import com.data.dataxer.repositories.qrepositories.QProjectRepository;
import com.data.dataxer.repositories.qrepositories.QSalaryRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.querydsl.core.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TimeServiceImpl implements TimeService {
    private final Long LIMIT = 5L;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TimeRepository timeRepository;

    @Autowired
    private QTimeRepository qTimeRepository;

    @Autowired
    private QSalaryRepository qSalaryRepository;

    @Autowired
    private QProjectRepository qProjectRepository;

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
        Salary salary = this.qSalaryRepository.getActiveSalary(SecurityUtils.loggedUser(), SecurityUtils.defaultProfileId());

        time.setSalary(salary);
        time.setUser(SecurityUtils.loggedUser());

        if (salary.getSalaryType().equals(SalaryType.FLAT)) {
            time.setPrice(null);
        } else {
            time.setPrice(BigDecimal.valueOf((float) time.getTime() / 60 / 60).multiply(salary.getPrice())); // calc total price by time store
        }
    }

    @Override
    public void destroy(Long id) {
        this.timeRepository.delete(this.getTimeByIdSimple(id));
    }

    @Override
    public Time getTimeById(Long id) {
        return this.qTimeRepository
                .getById(id, SecurityUtils.id(), SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Time not found!"));
    }

    @Override
    public Time getTimeByIdSimple(Long id) {
        return this.qTimeRepository
                .getByIdSimple(id, SecurityUtils.id(), SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Time not found"));
    }

    @Override
    public Page<Time> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qTimeRepository
                .paginate(pageable, rqlFilter, sortExpression, SecurityUtils.id(), SecurityUtils.defaultProfileId());
    }

    @Override
    public List<Time> allForPeriod(String rqlFilter) {
        return this.qTimeRepository.allForPeriod(rqlFilter, SecurityUtils.id(), SecurityUtils.defaultProfileId());
    }

    @Override
    public Time getLastUserTime() {
        return this.timeRepository.findFirstByUserIdAndAppProfileIdAndDateWorkOrderByIdDesc(SecurityUtils.id(), SecurityUtils.defaultProfileId(), LocalDate.now());
    }

    @Override
    public List<Integer> getAllYears() {
        return this.qTimeRepository.getAllYears(SecurityUtils.defaultProfileId());
    }

    @Override
    public List<Time> allByUser(String userUid) {
        return this.timeRepository.findAllByCompanyIdAndUserUid(SecurityUtils.defaultProfileId(), userUid);
    }

    @Override
    public List<Time> allByProject(Long projectId) {
        return this.qTimeRepository.getAllProjectTimes(projectId, SecurityUtils.defaultProfileId());
    }

    @Override
    public List<Time> allByProject(Long projectId, List<Long> companyIds) {
        return this.qTimeRepository.getAllProjectTimesOrdered(projectId, SecurityUtils.defaultProfileId());
    }

    @Override
    public List<MonthAndYearDTO> getAllUserMonths(Long userId) {
        List<MonthAndYearDTO> response = new ArrayList<>();
        List<Tuple> yearsAndMonths = this.qTimeRepository.getAllUserMonths(userId, SecurityUtils.defaultProfileId());

        yearsAndMonths.forEach(
                tuple -> response.add(new MonthAndYearDTO(tuple.get(QTime.time1.dateWork.year()), tuple.get(QTime.time1.dateWork.month())))
        );

        return response;
    }

    @Override
    public List<Project> getAllUserProjects(Long userId) {
        return this.qTimeRepository.getAllUserProjects(userId, SecurityUtils.defaultProfileId());
    }

    @Override
    public List<Category> lastProjectCategories(Long projectId) {
        List<Long> categoryIds = timeRepository.loadLastUserCategories(projectId, SecurityUtils.uid(), SecurityUtils.defaultProfileId(), LIMIT);

        List<Category> categories = this.categoryRepository.findAllByIdInAndAppProfileId(categoryIds, SecurityUtils.defaultProfileId());

        categories.sort(Comparator.comparing(category -> categoryIds.indexOf(category.getId())));

        return categories;
    }

    @Override
    public List<Project> getLastUserWorkingProjects(Long userId) {
        List<Long> projectIds = this.timeRepository.loadLastUserProject(SecurityUtils.uid(), LIMIT, SecurityUtils.defaultProfileId());

        List<Project> projects = this.qProjectRepository.getAllByIds(projectIds, SecurityUtils.defaultProfileId());

        projects.sort(Comparator.comparing(project -> projectIds.indexOf(project.getId())));

        return projects;
    }
}
