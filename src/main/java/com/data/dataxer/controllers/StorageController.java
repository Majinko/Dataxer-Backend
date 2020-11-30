package com.data.dataxer.controllers;

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

    @GetMapping("/{path}")
    protected ResponseEntity<Resource> avatar(@PathVariable String path) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("fff"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=")
                .body(new ByteArrayResource(storageService.getFile(path)));
    }
}
