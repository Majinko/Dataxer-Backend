package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findByIdAndCompanyIdIn(Long id, List<Long> companyIds);

    Optional<List<Category>> findAllByDepthGreaterThanAndCompanyIdInOrderByDepthAscPositionAsc(Integer depth, List<Long> companyIds);

    Optional<List<Category>> findAllByDepthGreaterThanAndCompanyIdInAndParentIsOrderByDepthAscPositionAsc(Integer depth, List<Long> companyIds, Category parent);

    @Query("SELECT c FROM Category c WHERE c.name = ?1 AND c.company.id = ?2")
    Optional<Category> findCategoryByName(String categoryName, Long companyId);
}
