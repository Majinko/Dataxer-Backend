package com.data.dataxer.controllers;

import com.data.dataxer.mappers.StorageMapper;
import com.data.dataxer.models.dto.StorageFileDTO;
import com.data.dataxer.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/storage")
@PreAuthorize("hasPermission(null, 'Storage', 'Storage')")
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
