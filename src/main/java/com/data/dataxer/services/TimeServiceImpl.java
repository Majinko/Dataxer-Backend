package com.data.dataxer.services;

import com.data.dataxer.models.domain.Time;
import com.data.dataxer.repositories.TimeRepository;
import com.data.dataxer.repositories.qrepositories.QSalaryRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TimeServiceImpl implements TimeService {

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
}
