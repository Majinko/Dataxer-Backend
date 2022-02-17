package com.data.dataxer.services;

import com.data.dataxer.models.domain.Item;
import com.data.dataxer.models.domain.ItemPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService  {
    Item store(Item item, ItemPrice itemPrice);

    Page<Item> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    Item update(Item item, ItemPrice itemPrice);

    void destroy(long id);

    Item getById(long id);

    Item getByIdSimple(long id);

    List<Item> search(String q);
}
