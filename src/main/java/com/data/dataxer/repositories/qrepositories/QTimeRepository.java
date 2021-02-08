package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Time;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QTimeRepository {
    Optional<Time> getById(Long id, Long companyId, Boolean disableFilter);

    Optional<Time> getByIdSimple(Long id, Long companyId, Boolean disableFilter);

    Page<Time> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long userId,  Long companyId, Boolean disableFilter);
}
