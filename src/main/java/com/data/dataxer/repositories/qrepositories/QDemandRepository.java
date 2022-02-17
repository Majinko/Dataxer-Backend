package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Demand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QDemandRepository {
   Page<Demand> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);

    Optional<Demand> getById(Long id, Long appProfileId);
}
