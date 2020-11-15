package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Time;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QTimeRepository {

    Optional<Time> getById(Long id, List<Long> companyIds);

    Optional<Time> getByIdSimple(Long id, List<Long> companyIds);

    Page<Time> paginate(Pageable pageable, Filter filter, List<Long> companyIds);

}
