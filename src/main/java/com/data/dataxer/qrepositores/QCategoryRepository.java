package com.data.dataxer.qrepositores;

import com.data.dataxer.models.domain.Category;

import java.util.List;

public interface QCategoryRepository {
    List<Category> findAllByCompanyIdIn(List<Long> companyIds);
}
