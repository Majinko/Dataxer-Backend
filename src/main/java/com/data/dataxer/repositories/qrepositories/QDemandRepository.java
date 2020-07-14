package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Demand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QDemandRepository {
   Page<Demand> paginate(Pageable pageable, List<Long> companyIds);

    Demand getById(Long id, List<Long> companyIds);
}
