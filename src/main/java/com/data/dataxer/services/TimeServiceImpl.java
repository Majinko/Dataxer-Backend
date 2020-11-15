package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Time;
import com.data.dataxer.repositories.TimeRepository;
import com.data.dataxer.repositories.qrepositories.QTimeRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TimeServiceImpl implements TimeService {

    private final TimeRepository timeRepository;
    private final QTimeRepository qTimeRepository;

    public TimeServiceImpl(TimeRepository timeRepository, QTimeRepository qTimeRepository) {
        this.timeRepository = timeRepository;
        this.qTimeRepository = qTimeRepository;
    }

    @Override
    public Time store(Time time) {
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
                .getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Time not found!"));
    }

    @Override
    public Time getTimeByIdSimple(Long id) {
        return this.qTimeRepository
                .getByIdSimple(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Time not found"));
    }

    @Override
    public Page<Time> paginate(Pageable pageable, Filter filter) {
        return this.qTimeRepository
                .paginate(pageable, filter, SecurityUtils.companyIds());
    }


}
