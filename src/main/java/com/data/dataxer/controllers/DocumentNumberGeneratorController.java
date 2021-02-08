package com.data.dataxer.controllers;

import com.data.dataxer.mappers.DocumentNumberGeneratorMapper;
import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.dto.DocumentNumberGeneratorDTO;
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
        return ResponseEntity.ok(documentNumberGeneratorMapper.documentNumberGeneratorToDocumentNumberGeneratorDTO(this.documentNumberGeneratorService.getById(id, false)));
    }

    @PostMapping("/store")
    public void store(@RequestBody DocumentNumberGeneratorDTO documentNumberGeneratorDTO) {
        this.documentNumberGeneratorService.store(this.documentNumberGeneratorMapper.documentNumberGeneratorDTOToDocumentNumberGenerator(documentNumberGeneratorDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody DocumentNumberGeneratorDTO documentNumberGeneratorDTO) {
        this.documentNumberGeneratorService.update(this.documentNumberGeneratorMapper.documentNumberGeneratorDTOToDocumentNumberGenerator(documentNumberGeneratorDTO));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<DocumentNumberGeneratorDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+documentNumberGenerator.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(this.documentNumberGeneratorService.paginate(pageable, rqlFilter, sortExpression, false)
                .map(this::convertToDocumentNumberGeneratorDTO));
    }

    @GetMapping("/generateNextByType/{documentType}")
    public ResponseEntity<String> generateNextNumberByDocumentType(
            @PathVariable DocumentType documentType
    ) {
        return ResponseEntity.ok(this.documentNumberGeneratorService.generateNextNumberByDocumentType(documentType, false, false));
    }

    @GetMapping("/generateAndSaveNextByType/{documentType}")
    public ResponseEntity<String> generateAndSaveNextNumberByDocumentType(
            @PathVariable DocumentType documentType
    ) {
        return ResponseEntity.ok(this.documentNumberGeneratorService.generateNextNumberByDocumentType(documentType, true, false));
    }

    @GetMapping("/generateNextById/{id}")
    public ResponseEntity<String> generateNextNumberByDocumentId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(this.documentNumberGeneratorService.generateNextNumberByDocumentId(id, false, false));
    }

    @GetMapping("/generateAndSaveNextById/{id}")
    public ResponseEntity<String> generateAndSaveNextNumberByDocumentId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(this.documentNumberGeneratorService.generateNextNumberByDocumentId(id, true, false));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.documentNumberGeneratorService.destroy(id);
    }

    @GetMapping("/resetGenerationByType/{documentType}")
    public void resetGenerationByType(@PathVariable DocumentType documentType) {
        this.documentNumberGeneratorService.resetGenerationByType(documentType, false);
    }

    @GetMapping("/resetGenerationById/{id}")
    public void resetGenerationById(@PathVariable Long id) {
        this.documentNumberGeneratorService.resetGenerationById(id, false);
    }

    private DocumentNumberGeneratorDTO convertToDocumentNumberGeneratorDTO(DocumentNumberGenerator documentNumberGenerator) {
        DocumentNumberGeneratorDTO documentNumberGeneratorDTO = this.documentNumberGeneratorMapper.documentNumberGeneratorToDocumentNumberGeneratorDTO(documentNumberGenerator);
        documentNumberGeneratorDTO.setNextNumber(this.documentNumberGeneratorService.getNextNumber(documentNumberGenerator));
        return documentNumberGeneratorDTO;
    }
}
