package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Pack;
import com.data.dataxer.models.domain.PackItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QPackRepository {
    Page<Pack> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds);

    Pack getById(Long id, List<Long> companyIds);

    List<Pack> search(String q, List<Long> companyIds);
}
