package com.data.dataxer.controllers;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.mappers.DocumentNumberGeneratorMapper;
import com.data.dataxer.models.dto.DocumentNumberGeneratorDTO;
import com.data.dataxer.models.dto.InvoiceDTO;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.services.DocumentNumberGeneratorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public ResponseEntity<DocumentNumberGeneratorDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(documentNumberGeneratorMapper.documentNumberGeneratorToDocumentNumberGeneratorDTO(this.documentNumberGeneratorService.getById(id)));
    }

    @PostMapping("/storeOrUpdate")
    public void storeOrUpdate(@RequestBody DocumentNumberGeneratorDTO documentNumberGeneratorDTO) {
        this.documentNumberGeneratorService.storeOrUpdate(this.documentNumberGeneratorMapper.documentNumberGeneratorDTOToDocumentNumberGenerator(documentNumberGeneratorDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody DocumentNumberGeneratorDTO documentNumberGeneratorDTO) {
        this.documentNumberGeneratorService.update(this.documentNumberGeneratorMapper.documentNumberGeneratorDTOToDocumentNumberGenerator(documentNumberGeneratorDTO));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<DocumentNumberGeneratorDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sortColumn,
            @RequestBody(required = false) Filter filter
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortColumn)));

        return ResponseEntity.ok(this.documentNumberGeneratorService.paginate(pageable, filter).map(this.documentNumberGeneratorMapper::documentNumberGeneratorToDocumentNumberGeneratorDTOSimple));
    }

    @GetMapping("/generateNextByType/{documentType}")
    public ResponseEntity<String> generateNextNumberByDocumentType(
            @PathVariable DocumentType documentType
    ) {
        return ResponseEntity.ok(this.documentNumberGeneratorService.generateNextNumberByDocumentType(documentType));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.documentNumberGeneratorService.destroy(id);
    }

    @GetMapping("/resetGenerationByType/{documentType}")
    public void resetGenerationByType(@PathVariable DocumentType documentType) {
        this.documentNumberGeneratorService.resetGenerationByType(documentType);
    }

    @GetMapping("/resetGenerationById/{id}")
    public void resetGenerationById(@PathVariable Long id) {
        this.documentNumberGeneratorService.resetGenerationById(id);
    }
}
