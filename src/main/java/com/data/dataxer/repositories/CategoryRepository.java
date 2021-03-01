package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    Category findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.id = ?1 AND c.company.id = ?2 AND c.depth > 0")
    Optional<Category> findCategoryByIdAndCompanyId(Long id, Long companyId);

    @Query("SELECT c FROM Category c WHERE c.company.id = ?1 AND c.depth > 0")
    List<Category> findAllByCompanyId(Long companyId);

    //depth 1 are all categories on first level
    @Query("SELECT c FROM Category c WHERE c.company.id = ?1 AND c.depth = 1 ORDER BY c.position ASC")
    List<Category> findAllByCompanyIdAndDepthOrderByPositionAsc(Long companyId);

    @Query("SELECT c FROM Category c WHERE c.company.id = ?1 AND c.depth = 0 ORDER BY c.position DESC")
    List<Category> findByCompanyAndDepthOrderByPositionDesc(Long companyId);

    @Transactional
    @Modifying
    @Query("UPDATE Category c SET c.position = c.position + ?2 WHERE c.id IN ?1 AND c.company.id = ?3")
    void updateCategoriesPosition(List<Long> ids, int value, Long companyId);

    @Transactional
    @Modifying
    @Query("UPDATE Category c SET c.position = ?2 WHERE c.id = ?1 AND c.company.id = ?3")
    void setCategoryPosition(Long id, Integer value, Long companyId);

    //category changes
    //check behavior if zero roots or too many
    @Query("SELECT c FROM Category c WHERE c.company.id = ?1 AND c.depth = 0")
    Optional<Category> findCompanyRoot(Long companyId);

}
