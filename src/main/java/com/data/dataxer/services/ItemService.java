package com.data.dataxer.services;

import com.data.dataxer.models.domain.Item;
import com.data.dataxer.repositories.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemService implements ItemServiceImpl {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item store(Item item) {
        return this.itemRepository.save(item);
    }
}
