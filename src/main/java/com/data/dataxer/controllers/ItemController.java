package com.data.dataxer.controllers;

import com.data.dataxer.mappers.ItemMapper;
import com.data.dataxer.models.domain.Item;
import com.data.dataxer.models.dto.ItemDTO;
import com.data.dataxer.services.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<ItemDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        return ResponseEntity.ok(itemService.paginate(pageable).map(itemMapper::toItemDto));
    }
}
