package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Category;

import java.util.List;
import java.util.Optional;

public interface QCategoryRepository {

    Optional<Category> getBaseRoot(Long companyId);

    Category findChildWithHighestRgt(Category parent, Long companyId);

    Category getById(Long id, Long companyId);

    List<Category> findByLftGreaterEqualThan(Integer lft, Long companyId);

    List<Category> findByLftGreaterThan(Integer lft, Long companyId);

    List<Category> findByRgtGreaterThan(Integer rgt, Long companyId);

    List<Category> findSubTree(Category category, Long companyId);

    List<Category> findSiblingsWithHigherPosition(Category category, Long companyId);

    long getCountOfChildren(Long id, Long companyId);

    void updateCategoryPosition(Long id, long i, Long companyId);

    List<Category> getAllOldRootCategories(Long companyId);

    List<Category> getOldCategoryChildren(Category processedCategory, Long companyId);

    void updateOldCategoryLftAndRgtToNull(Long companyId);
}
