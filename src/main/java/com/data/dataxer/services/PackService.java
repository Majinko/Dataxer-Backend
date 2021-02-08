package com.data.dataxer.services;

import com.data.dataxer.models.domain.Pack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PackService {
    void store(Pack pack);

    Pack getByIdSimple(Long id);

    Page<Pack> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter);

    void destroy(Long id);

    Pack getById(Long id, Boolean disableFilter);

    void update(Pack packDTOtoPack);

    List<Pack> search(String q);
}
