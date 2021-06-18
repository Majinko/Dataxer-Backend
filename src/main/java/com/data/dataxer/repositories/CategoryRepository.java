package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Optional<List<Category>> findByParentIdIsNullAndCompanyId(Long companyId);

    Optional<Category> findByIdAndCompanyId(Long id, Long companyId);

    List<Category> findAllByCompanyId(Long companyId);

    List<Category> findAllByIdInAndCompanyId(List<Long> ids, Long companyId);

    @Query("SELECT c FROM Category c WHERE c.name = ?1 AND c.company.id = ?2")
    Optional<Category> findCategoryByName(String categoryName, Long companyId);


    @Query(
            value = "WITH RECURSIVE ids (id) as (SELECT category.id from category where id = ?1 UNION ALL SELECT category.id from ids, category where category.company_id = ?2 and category.parent_id = ids.id) SELECT * FROM ids where ids.id in (SELECT category_id from time);",
            nativeQuery = true)
    List<Long> findAllChildIds(Long categoryId, Long companyId);
}
