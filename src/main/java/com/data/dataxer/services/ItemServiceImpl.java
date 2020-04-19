package com.data.dataxer.services;

import com.data.dataxer.models.domain.Item;
import com.data.dataxer.repositories.ItemRepository;
import com.data.dataxer.securityContextUtils.SecurityContextUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item store(Item item) {
        return this.itemRepository.save(item);
    }

    @Override
    public Page<Item> paginate(Pageable pageable) {
        return itemRepository.findAllByCompanyIdIn(pageable, SecurityContextUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Items not found"));
    }

    @Override
    public void delete(Long id) {
        Item item = itemRepository.findByIdAndCompanyIdIn(id, SecurityContextUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setDeletedAt(LocalDateTime.now());
        itemRepository.save(item);
    }
}
