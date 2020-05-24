package com.data.dataxer.services;

import com.data.dataxer.models.domain.Pack;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PackService {
    void store(Pack pack);

    Pack getByIdSimple(Long id);

    Page<Pack> paginate(Pageable pageable);

    void destroy(Long id);

    Pack getById(Long id);

    void update(Pack packDTOtoPack);
}
