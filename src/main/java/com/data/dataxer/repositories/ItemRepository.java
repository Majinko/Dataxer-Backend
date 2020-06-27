package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByIdAndCompanyIdIn(Long id, List<Long> companyIds);
    Optional<List<Item>> findAllByTitleContainsAndCompanyIdIn(String title, List<Long> companyIds);
    Optional<Page<Item>> findAllByCompanyIdIn(Pageable pageable, List<Long> companyIds);
}