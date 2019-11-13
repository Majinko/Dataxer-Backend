package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
