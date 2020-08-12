package com.data.dataxer.controllers;

import com.data.dataxer.mappers.DocumentNumberGeneratorMapper;
import com.data.dataxer.models.dto.DocumentNumberGeneratorDTO;
import com.data.dataxer.services.DocumentNumberGeneratorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/numberGenerator")
public class DocumentNumberGeneratorController {

    private final DocumentNumberGeneratorService documentNumberGeneratorService;
    private final DocumentNumberGeneratorMapper documentNumberGeneratorMapper;

    public DocumentNumberGeneratorController(DocumentNumberGeneratorService documentNumberGeneratorService, DocumentNumberGeneratorMapper documentNumberGeneratorMapper) {
        this.documentNumberGeneratorService = documentNumberGeneratorService;
        this.documentNumberGeneratorMapper = documentNumberGeneratorMapper;
    }

    @PostMapping("/store")
    public void store(@RequestBody DocumentNumberGeneratorDTO documentNumberGeneratorDTO) {
        this.documentNumberGeneratorService.store(this.documentNumberGeneratorMapper.documentNumberGeneratorDTOToDocumentNumberGenerator(documentNumberGeneratorDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody DocumentNumberGeneratorDTO documentNumberGeneratorDTO) {
        this.documentNumberGeneratorService.update(this.documentNumberGeneratorMapper.documentNumberGeneratorDTOToDocumentNumberGenerator(documentNumberGeneratorDTO));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.documentNumberGeneratorService.destroy(id);
    }

}
