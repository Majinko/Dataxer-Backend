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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/storage")
public class StorageController {
    @Autowired
    StorageService storageService;

    @Autowired
    StorageMapper storageMapper;

    @GetMapping("/preview/{id}/{type}")
    protected ResponseEntity<Resource> preview(@PathVariable Long id, @PathVariable String type) {
        StorageFileDTO storageFileDTO = storageMapper.storageToStorageFileDTO(storageService.getPreview(id, type, true));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(storageFileDTO.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + storageFileDTO.getFileName() + "\"")
                .body(new ByteArrayResource(storageFileDTO.getContent()));
    }

    @GetMapping("/{id}/{type}")
    protected ResponseEntity<StorageFileDTO> getStoragePreviewFile(@PathVariable Long id, @PathVariable String type){
        return ResponseEntity.ok(storageMapper.storageToStorageFileDTO(this.storageService.getPreview(id, type, false)));
    }
}
