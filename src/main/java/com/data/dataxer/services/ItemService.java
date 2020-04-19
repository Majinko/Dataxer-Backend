package com.data.dataxer.services;

import com.data.dataxer.models.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

public interface ItemService  {
    Item store(Item item);

    Page<Item> paginate(Pageable pageable);

    void delete(Long id);
}
