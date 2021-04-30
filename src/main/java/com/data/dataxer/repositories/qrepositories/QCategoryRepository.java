package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Category;

import java.util.List;

public interface QCategoryRepository {

    Category getBaseRoot(Long companyId);

    Category findChildWithHighestRgt(Category parent, Long companyId);

    Category getById(Long id, Long companyId);

    List<Category> findByLftGreaterEqualThan(Integer lft, Long companyId);

    List<Category> findByLftGreaterThan(Integer lft, Long companyId);

    List<Category> findByRgtGreaterThan(Integer rgt, Long companyId);

    List<Category> findSubTree(Category category, Long companyId);

}
