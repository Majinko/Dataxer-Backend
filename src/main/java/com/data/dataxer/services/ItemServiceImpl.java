package com.data.dataxer.services;

import com.data.dataxer.models.domain.Item;
import com.data.dataxer.models.domain.ItemPrice;
import com.data.dataxer.repositories.ItemPriceRepository;
import com.data.dataxer.repositories.ItemRepository;
import com.data.dataxer.repositories.qrepositories.QItemRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final QItemRepository qItemRepository;
    private final ItemPriceRepository itemPriceRepository;

    public ItemServiceImpl(ItemRepository itemRepository, QItemRepository qItemRepository, ItemPriceRepository itemPriceRepository) {
        this.itemRepository = itemRepository;
        this.qItemRepository = qItemRepository;
        this.itemPriceRepository = itemPriceRepository;
    }

    @Override
    public void store(Item item, ItemPrice itemPrice) {
        this.itemRepository.save(item);

        // store item price
        if (itemPrice != null) {
            itemPrice.setItem(item);
            itemPriceRepository.save(itemPrice);
        }
    }

    @Override
    public Page<Item> paginate(Pageable pageable) {
        return itemRepository.findAllByCompanyIdIn(pageable, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Items not found"));
    }

    @Override
    public void update(Item item, ItemPrice itemPrice) {
        itemRepository.save(item);

        // update item price
        if (itemPrice != null) {
            itemPrice.setItem(item);
            itemPriceRepository.save(itemPrice);
        }
    }

    @Override
    public void destroy(long id) {
        Item item = this.getByIdSimple(id);

        itemRepository.delete(item);
    }

    @Override
    public Item getById(long id) {
        return this.qItemRepository.getById(id, SecurityUtils.companyIds());
    }

    @Override
    public Item getByIdSimple(long id) {
        return this.itemRepository.findByIdAndCompanyIdIn(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public List<Item> search(String q) {
        return this.qItemRepository.findAllByTitleContainsAndCompanyIdIn(q, SecurityUtils.companyIds())
                .orElse(null);
    }
}
