package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(
            value = "SELECT * FROM category WHERE parent_id is null",
            nativeQuery = true)
    Optional<List<Category>> nested();
}
