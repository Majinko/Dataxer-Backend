package com.data.dataxer.controllers;

import com.data.dataxer.mappers.StorageMapper;
import com.data.dataxer.models.dto.StorageFileDTO;
import com.data.dataxer.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/storage")
public class StorageController {
    @Autowired
    StorageService storageService;

    @Autowired
    StorageMapper storageMapper;

    @GetMapping("/{id}/{type}")
    protected ResponseEntity<StorageFileDTO> getStoragePreviewFile(@PathVariable Long id, @PathVariable String type) {
        return ResponseEntity.ok(storageMapper.storageToStorageFileDTO(this.storageService.getPreview(id, type)));
    }

    @GetMapping("/{id}")
    protected ResponseEntity<StorageFileDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(storageMapper.storageToStorageFileDTO(this.storageService.getById(id)));
    }


    @GetMapping("/destroy/{id}")
    protected void destroy(@PathVariable Long id) {
        this.storageService.destroy(id);
    }
}
