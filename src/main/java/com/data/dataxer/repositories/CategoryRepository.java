package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    Category findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.id = ?1 AND c.company.id = ?2")
    Category findCategoryByIdAndCompanyId(Long id, Long companyId);

    @Query("SELECT c FROM Category c WHERE c.company.id = ?1")
    List<Category> findAllByCompanyId(Long companyId);

    @Query("SELECT c FROM Category c WHERE c.company.id = ?1 AND c.depth = 0")
    List<Category> findAllByCompanyIdAndDepthOrderByPositionAsc(Long companyId);

    @Transactional
    @Modifying
    @Query("UPDATE Category c SET c.position = c.position + ?2 WHERE c.id IN ?1 AND c.company.id = ?3")
    void updateCategoriesPosition(List<Long> ids, int value, Long companyId);

    @Transactional
    @Modifying
    @Query("UPDATE Category c SET c.position = ?2 WHERE c.id = ?1 AND c.company.id = ?3")
    void setCategoryPosition(Long id, Integer value, Long companyId);

}
