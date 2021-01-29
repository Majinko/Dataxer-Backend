package com.data.dataxer.controllers;

import com.data.dataxer.mappers.ItemMapper;
import com.data.dataxer.mappers.ItemPriceMapper;
import com.data.dataxer.mappers.StorageMapper;
import com.data.dataxer.models.dto.ItemDTO;
import com.data.dataxer.models.dto.UploadContextDTO;
import com.data.dataxer.services.ItemService;
import com.data.dataxer.services.storage.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final ItemPriceMapper itemPriceMapper;
    private final StorageService storageService;
    private final StorageMapper storageMapper;

    public ItemController(ItemService itemService, ItemMapper itemMapper, ItemPriceMapper itemPriceMapper, StorageService storageService, StorageMapper storageMapper) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.itemPriceMapper = itemPriceMapper;
        this.storageService = storageService;
        this.storageMapper = storageMapper;
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<ItemDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+item.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        return ResponseEntity.ok(itemService.paginate(pageable, rqlFilter, sortExpression).map((itemMapper::itemToItemDtoSimple)));
    }

    @PostMapping("/store")
    public void store(
            @RequestBody UploadContextDTO<ItemDTO> uploadContext
    ) {
        ItemDTO item = itemMapper.itemToItemDto(this.itemService.store(itemMapper.toItem(uploadContext.getObject()), itemPriceMapper.toItemPrice(uploadContext.getObject().getItemPrice())));

        if (uploadContext.getObject().getPreview() != null) {
            this.storageService.store(storageMapper.storageFileDTOtoStorage(uploadContext.getObject().getPreview()), item.getId(), "item");
        }
    }

    @PostMapping("/update")
    public void update(@RequestBody ItemDTO itemDTO) {
        ItemDTO item = itemMapper.itemToItemDto(this.itemService.update(itemMapper.toItem(itemDTO), itemPriceMapper.toItemPrice(itemDTO.getItemPrice())));

        if (itemDTO.getPreview() != null) {
            this.storageService.destroy(item.getId(), "item"); // destroy old item if exist new
            this.storageService.store(storageMapper.storageFileDTOtoStorage(itemDTO.getPreview()), item.getId(), "item");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDTO> getById(@PathVariable long id) {
        return ResponseEntity.ok(itemMapper.itemToItemDto(this.itemService.getById(id)));
    }

    @GetMapping("/search/{q}")
    public ResponseEntity<List<ItemDTO>> search(@PathVariable String q) {
        return ResponseEntity.ok(itemMapper.itemsToItemsDTOsWithPrice(itemService.search(q)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable long id) {
        this.itemService.destroy(id);
    }
}
