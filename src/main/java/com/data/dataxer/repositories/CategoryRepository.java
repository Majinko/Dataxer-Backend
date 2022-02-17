package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.enums.CategoryGroup;
import com.data.dataxer.models.enums.CategoryType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<List<Category>> findByParentIdIsNullAndAppProfileId(Long appProfileId);

    Optional<Category> findByIdAndAppProfileId(Long id, Long appProfileId);

    List<Category> findAllByAppProfileIdOrderByPositionAsc(Long appProfileId);

    List<Category> findAllByIdInAndAppProfileId(List<Long> ids, Long appProfileId);

    List<Category> findAllByIdInAndAppProfileIdInOrderByPosition(List<Long> ids, Long appProfileId);

    List<Category> findAllByParentIdAndAppProfileId(Long parentId, Long appProfileId);

    List<Category> findAllByCategoryTypeInAndAppProfileId(List<CategoryType> categoryTypes, Long appProfileId);

    List<Category> findAllByCategoryGroupInAndAppProfileIdOrderByPosition(List<CategoryGroup> categoryGroups, Long appProfileId);

    List<Category> findAllByCategoryGroupAndAppProfileIdAndParentIdIsNullOrderByPosition(CategoryGroup categoryGroup, Long appProfileId);

    @Query("select c.id from Category c where c.categoryType in ?1 and c.appProfile.id = ?2")
    List<Long> findAllIdsCategoryTypeInAndAppProfileId(List<CategoryType> categoryTypes, Long appProfileId);

    @Query("SELECT c FROM Category c WHERE c.name = ?1 AND c.appProfile.id = ?2")
    Optional<Category> findCategoryByName(String categoryName, Long appProfile);

    @Query(
            value = "WITH RECURSIVE ids (id) as (SELECT category.id from category where id = ?1 UNION ALL SELECT category.id from ids, category where category.app_profile_id = ?2 and category.deleted_at is null and category.parent_id = ids.id) SELECT * FROM ids;",
            nativeQuery = true)
    List<Long> findAllChildIds(Long categoryId, Long appProfileId);

    @Query(
            value = "WITH RECURSIVE ids (id) as (SELECT category.id from category where id = ?1 UNION ALL SELECT category.id from ids, category where  category.app_profile_id = ?2 and category.parent_id = ids.id) SELECT * FROM ids where ids.id in (SELECT category_id from time);",
            nativeQuery = true)
    List<Long> findAllChildIdsHasTime(Long categoryId, Long appProfileId);

    @Query("SELECT c FROM Category c where c.categoryType = ?1 and c.appProfile.id = ?2")
    List<Category> findAllByType(CategoryType categoryType, Long appProfileId);
}
