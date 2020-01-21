package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    /*@EntityGraph(attributePaths = "children")*/
    Optional<List<Category>> findAllByCompanyIdIn(List<Long> companyIds);

    Optional<List<Category>> findAllByDeletedAtIsNull();

    @Query(
            value = "SELECT * FROM category WHERE parent_id is null and company_id IN :companyIds",
            nativeQuery = true)
    Optional<List<Category>> nested(@Param("companyIds") List<Long> companyIds);
}
