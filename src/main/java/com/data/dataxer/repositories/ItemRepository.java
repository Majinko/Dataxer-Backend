package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends CrudRepository<Item, Long> {
    Optional<Item> findByIdAndAppProfileId(Long id, Long appProfileId);

    @Query("SELECT i FROM Item i left join fetch i.categories")
    List<Item> findAllWithCategories();

    Optional<List<Item>> findAllByTitleContainsAndAppProfileId(String title, Long appProfileId);

    @Query("SELECT DISTINCT i, s FROM Item i LEFT JOIN  Storage s ON s.fileAbleId = i.id WHERE i.appProfile.id = ?1")
    List<Item> findAllByAppProfileId(Long appProfileId);
}
