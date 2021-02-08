package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends CrudRepository<Item, Long> {
    Optional<Item> findByIdAndCompanyId(Long id, Long companyId);

    Optional<List<Item>> findAllByTitleContainsAndCompanyIdIn(String title, List<Long> companyIds);

    @Query("SELECT DISTINCT i, s FROM Item i LEFT JOIN  Storage s ON s.fileAbleId = i.id WHERE i.company.id IN ?1")
    List<Item> findAllByCompanyIdIn(List<Long> companyIds);

    @Query("select DISTINCT i from Item i left join fetch i.storage where i.company.id in ?1")
    List<Item> findAllItemWithStorage(Pageable pageable, List<Long> companyIds);
}
