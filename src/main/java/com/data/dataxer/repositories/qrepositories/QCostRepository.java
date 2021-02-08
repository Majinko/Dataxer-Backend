package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Cost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QCostRepository {

    Page<Cost> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId, Boolean disableFilter);

    Optional<Cost> getById(Long id, Long companyId, Boolean disableFilter);

    Optional<Cost> getByIdWithRelation(Long id, Long companyId, Boolean disableFilter);
}
