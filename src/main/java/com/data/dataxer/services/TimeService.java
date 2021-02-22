package com.data.dataxer.services;

import com.data.dataxer.models.domain.Time;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TimeService {
    Time store(Time time);

    Time update(Time time);

    void destroy(Long id);

    Time getTimeById(Long id);

    Time getTimeByIdSimple(Long id);

    Page<Time> paginate(Pageable pageable, String rqlFilter, String sortExpression);
}
