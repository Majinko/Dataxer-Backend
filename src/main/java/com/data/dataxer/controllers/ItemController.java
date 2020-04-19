package com.data.dataxer.controllers;

import com.data.dataxer.mappers.ItemMapper;
import com.data.dataxer.models.domain.Item;
import com.data.dataxer.models.dto.ItemDTO;
import com.data.dataxer.services.ItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    public ItemController(ItemService itemService, ItemMapper itemMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @PostMapping("/store")
    public void store(@RequestBody ItemDTO itemDTO) {
        Item item = this.itemService.store(itemMapper.toItem(itemDTO));
    }
}
