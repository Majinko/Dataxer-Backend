package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.enums.CategoryType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<List<Category>> findByParentIdIsNullAndCompanyId(Long companyId);

    Optional<Category> findByIdAndCompanyId(Long id, Long companyId);

    List<Category> findAllByCompanyIdOrderByPositionAsc(Long companyId);

    List<Category> findAllByIdInAndCompanyId(List<Long> ids, Long companyId);

    List<Category> findAllByParentIdAndCompanyId(Long parentId, Long companyId);

    List<Category> findAllByIsInProjectOverviewAndCompanyIdIn(Boolean isInProjectOverview, List<Long> companyIds);

    @Query("SELECT c FROM Category c WHERE c.name = ?1 AND c.company.id = ?2")
    Optional<Category> findCategoryByName(String categoryName, Long companyId);

    @Query(
            value = "WITH RECURSIVE ids (id) as (SELECT category.id from category where id = ?1 UNION ALL SELECT category.id from ids, category where category.company_id = ?2 and category.deleted_at is null and category.parent_id = ids.id) SELECT * FROM ids;",
            nativeQuery = true)
    List<Long> findAllChildIds(Long categoryId, Long companyId);

    @Query(
            value = "WITH RECURSIVE ids (id) as (SELECT category.id from category where id = ?1 UNION ALL SELECT category.id from ids, category where category.company_id = ?2 and category.parent_id = ids.id) SELECT * FROM ids where ids.id in (SELECT category_id from time);",
            nativeQuery = true)
    List<Long> findAllChildIdsHasTime(Long categoryId, Long companyId);

    @Query("SELECT c FROM Category c where c.categoryType = ?1 and c.company.id in ?2")
    List<Category> findAllByType(CategoryType categoryType, List<Long> companyIds);
}
