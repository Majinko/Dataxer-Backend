package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Pack;
import com.data.dataxer.models.domain.PackItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QPackRepository {
    Page<Pack> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);

    Pack getById(Long id, Long appProfileId);

    List<Pack> search(String q, Long appProfileId);
}
