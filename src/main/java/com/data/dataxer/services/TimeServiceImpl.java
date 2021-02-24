package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.QTime;
import com.data.dataxer.models.domain.Time;
import com.data.dataxer.models.dto.MonthAndYearDTO;
import com.data.dataxer.repositories.TimeRepository;
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
import java.util.stream.Collectors;

@Service
public class TimeServiceImpl implements TimeService {

    private final Long LIMIT = 5L;

    private final TimeRepository timeRepository;
    private final QTimeRepository qTimeRepository;
    private final QSalaryRepository qSalaryRepository;

    public TimeServiceImpl(TimeRepository timeRepository, QTimeRepository qTimeRepository,
                           QSalaryRepository qSalaryRepository) {
        this.timeRepository = timeRepository;
        this.qTimeRepository = qTimeRepository;
        this.qSalaryRepository = qSalaryRepository;
    }

    @Override
    public Time store(Time time) {
        BigDecimal price = this.qSalaryRepository.getPriceFromSalaryByUserFinishIsNull(time.getUser(), SecurityUtils.companyId());
        time.setPrice(price);

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
    public List<Category> getProjectCategoryByTime(Long projectId) {
        return this.qTimeRepository.getTimesForProjectCategoryOrderByDate(projectId, SecurityUtils.companyId()).stream()
                .map(Time::getCategory).collect(Collectors.toList());
    }

    @Override
    public List<Category> getProjectCategoryByPosition(Long projectId) {
        return this.qTimeRepository.getTimesForProjectCategoryOrderByPosition(projectId, SecurityUtils.companyId()).stream()
                .map(Time::getCategory).collect(Collectors.toList());
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
        return this.qTimeRepository.getUserLastProjects(userId, LIMIT, SecurityUtils.companyId());
    }
}
