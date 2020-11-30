package com.data.dataxer.controllers;

import com.data.dataxer.mappers.ItemMapper;
import com.data.dataxer.mappers.ItemPriceMapper;
import com.data.dataxer.models.dto.ItemDTO;
import com.data.dataxer.models.dto.StorageFileDTO;
import com.data.dataxer.models.dto.UploadContextDTO;
import com.data.dataxer.services.ItemService;
import com.data.dataxer.services.storage.StorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/item")
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final ItemPriceMapper itemPriceMapper;
    private final StorageService storageService;

    public ItemController(ItemService itemService, ItemMapper itemMapper, ItemPriceMapper itemPriceMapper, StorageService storageService) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.itemPriceMapper = itemPriceMapper;
        this.storageService = storageService;
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<ItemDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        return ResponseEntity.ok(itemService.paginate(pageable).map((itemMapper::itemToItemDtoSimple)));
    }

    @PostMapping("/store")
    public void store(
            @RequestBody UploadContextDTO<ItemDTO> uploadContext
    ) {
        ItemDTO item = itemMapper.itemToItemDto(this.itemService.store(itemMapper.toItem(uploadContext.getObject()), itemPriceMapper.toItemPrice(uploadContext.getObject().getItemPrice())));

        if (uploadContext.getFiles().size() > 0) {
            uploadContext.getFiles().forEach(file -> {
                this.storageService.store(file, item.getId(), "item");
            });
        }
    }

    @PostMapping("/update")
    public void update(@RequestBody ItemDTO itemDTO) {
        this.itemService.update(itemMapper.toItem(itemDTO), itemPriceMapper.toItemPrice(itemDTO.getItemPrice()));
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
