package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    Category findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.id = ?1 AND c.company.id = ?2")
    Category findCategoryByIdAndCompanyId(Long id, Long companyId);

    @Query("SELECT c FROM Category c WHERE c.company.id = ?1")
    List<Category> findAllByCompanyId(Long companyId);

    @Query("SELECT c FROM Category c WHERE c.company.id = ?1 AND c.parent IS NULL")
    List<Category> findAllByCompanyIdAndParentIsNull(Long companyId);

}
