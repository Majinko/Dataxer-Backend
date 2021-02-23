package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.Time;
import com.data.dataxer.repositories.TimeRepository;
import com.data.dataxer.repositories.qrepositories.QSalaryRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TimeServiceImpl implements TimeService {

    private final long LIMIT = 5000;

    private final TimeRepository timeRepository;
    private final QTimeRepository qTimeRepository;
    private final QSalaryRepository qSalaryRepository;

    public TimeServiceImpl(TimeRepository timeRepository, QTimeRepository qTimeRepository, QSalaryRepository qSalaryRepository) {
        this.timeRepository = timeRepository;
        this.qTimeRepository = qTimeRepository;
        this.qSalaryRepository = qSalaryRepository;
    }

    @Override
    public Time store(Time time) {
        BigDecimal price = this.qSalaryRepository.getPriceFromSalaryByUserFinishIsNull(SecurityUtils.loggedUser(), SecurityUtils.companyId());

        time.setUser(SecurityUtils.loggedUser());
        time.setPrice(BigDecimal.valueOf((float)time.getTime() / 60 / 60).multiply(price)); // calc total price by time store

        return this.timeRepository.save(time);
    }

    @Override
    public Time update(Time time) {
        return this.timeRepository.save(time);
    }

    @Override
    public void destroy(Long id) {
        this.timeRepository.delete(this.getTimeByIdSimple(id));
    }

    @Override
    public Time getTimeById(Long id) {
        return this.qTimeRepository
                .getById(id, SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Time not found!"));
    }

    @Override
    public Time getTimeByIdSimple(Long id) {
        return this.qTimeRepository
                .getByIdSimple(id, SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Time not found"));
    }

    @Override
    public Page<Time> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qTimeRepository
                .paginate(pageable, rqlFilter, sortExpression, SecurityUtils.id(), SecurityUtils.companyId());
    }

    @Override
    public List<Time> allForPeriod(LocalDate from, LocalDate to) {
        return this.qTimeRepository.allForPeriod(from ,to, SecurityUtils.companyId());
    }

    @Override
    public List<Time> allUserTimesForPeriod(LocalDate date, Long userId) {
        return this.qTimeRepository.allUserTimesForPeriod(date.withDayOfMonth(1),
                date.withDayOfMonth(date.lengthOfMonth()), userId, SecurityUtils.companyId());
    }

    @Override
    public List<Project> allUniqueUserProjectsFromTimes(List<Time> userTimes) {
        Set<Project> userProjects = new HashSet<>();
        userTimes.forEach(time -> userProjects.add(time.getProject()));

        return List.copyOf(userProjects);
    }

    @Override
    public List<Project> getLastUserWorkingProjects(Long userId) {
        Set<Project> userProject = new HashSet<>();
        List<Time> userTimes;
        long offset = 0;
        do {
            userTimes = this.qTimeRepository.getUserLastProjects(userId,offset, offset + LIMIT, SecurityUtils.companyId());
            userProject.addAll(this.allUniqueUserProjectsFromTimes(userTimes));
        } while(userProject.size() < 5 && userTimes.size() == LIMIT);
        return userProject.stream().limit(5).collect(Collectors.toList());
    }

    @Override
    public List<Category> getProjectCategoryByTime(Long projectId) {
        return this.qTimeRepository.getTimesForProjectCategoryOrderByDate(projectId, SecurityUtils.companyId()).stream()
                .map(Time::getCategory).collect(Collectors.toList());
    }

    @Override
    public List<Category> getProjectCategoryByPosition(Long projectId) {
        return this.qTimeRepository.getTimesForProjectCategoryOrderByPosition(projectId, SecurityUtils.companyId()).stream()
                .map(Time::getCategory).collect(Collectors.toList());
    }
}
