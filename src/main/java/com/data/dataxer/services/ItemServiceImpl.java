package com.data.dataxer.services;

import com.data.dataxer.models.domain.Item;
import com.data.dataxer.models.domain.ItemPrice;
import com.data.dataxer.repositories.ItemPriceRepository;
import com.data.dataxer.repositories.ItemRepository;
import com.data.dataxer.repositories.qrepositories.QItemRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private QItemRepository qItemRepository;

    @Autowired
    private ItemPriceRepository itemPriceRepository;

    @Transactional
    public Item store(Item item, ItemPrice itemPrice) {
        this.itemRepository.save(item);

        // store item price
        if (itemPrice != null) {
            itemPrice.setItem(item);
            itemPriceRepository.save(itemPrice);
        }

        return item;
    }

    @Override
    public Page<Item> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return qItemRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.defaultProfileId());
    }

    @Override
    public Item update(Item item, ItemPrice itemPrice) {
        // update item price
        if (itemPrice != null) {
            itemPrice.setItem(item);
            itemPriceRepository.save(itemPrice);
        }

        this.updateItem(item);

        return item;
    }

    @Override
    public void destroy(long id) {
        Item item = this.getByIdSimple(id);

        itemRepository.delete(item);
    }

    @Override
    public Item getById(long id) {
        return this.qItemRepository.getById(id, SecurityUtils.defaultProfileId());
    }

    @Override
    public Item getByIdSimple(long id) {
        return this.itemRepository.findByIdAndAppProfileId(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    public List<Item> search(String q) {
        return this.qItemRepository
                .findAllByTitleContainsAndAppProfileId(q, SecurityUtils.defaultProfileId())
                .orElse(null);
    }

    private void updateItem(Item item) {
        this.itemRepository.findById(item.getId()).map(i -> {

            i.setCategories(item.getCategories());
            i.setSupplier(item.getSupplier());
            i.setTitle(item.getTitle());
            i.setType(item.getType());
            i.setShortDescription(item.getShortDescription());
            i.setDescription(item.getDescription());
            i.setManufacturer(item.getManufacturer());
            i.setWeb(item.getWeb());
            i.setUnit(item.getUnit());
            i.setCode(item.getCode());
            i.setDimensions(item.getDimensions());
            i.setIsPartOfSet(item.getIsPartOfSet());
            i.setNeedMontage(item.getNeedMontage());
            i.setPriceLevel(item.getPriceLevel());
            i.setModel(item.getModel());
            i.setSeries(item.getSeries());
            i.setColor(item.getColor());
            i.setMaterial(item.getMaterial());

            return itemRepository.save(i);
        });
    }
}
