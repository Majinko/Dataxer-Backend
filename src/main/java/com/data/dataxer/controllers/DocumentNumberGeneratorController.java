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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/numberGenerator")
@PreAuthorize("hasPermission(null, 'Document', 'Document')")
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

        return ResponseEntity.ok(this.documentNumberGeneratorService.paginate(pageable, rqlFilter, sortExpression).map(this::convertToDocumentNumberGeneratorDTO));
    }

    @GetMapping("/generateNextByType/{documentType}")
    public ResponseEntity<String> generateNextNumberByDocumentType(
            @PathVariable DocumentType documentType,
            @RequestParam(value = "companyId", required = true) Long companyId,
            @RequestParam(value = "generationDate", required = false) LocalDate generationDate
            ) {
        if (generationDate == null) {
            generationDate = LocalDate.now();
        }
        return ResponseEntity.ok(this.documentNumberGeneratorService.generateNextNumberByDocumentType(documentType, generationDate, companyId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<DocumentNumberGeneratorDTO>> getAll() {
        return ResponseEntity.ok(this.documentNumberGeneratorService.getAll().stream().map(this::convertToDocumentNumberGeneratorDTO).collect(Collectors.toList()));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.documentNumberGeneratorService.destroy(id);
    }

    private DocumentNumberGeneratorDTO convertToDocumentNumberGeneratorDTO(DocumentNumberGenerator documentNumberGenerator) {
        DocumentNumberGeneratorDTO documentNumberGeneratorDTO = this.documentNumberGeneratorMapper.documentNumberGeneratorToDocumentNumberGeneratorDTO(documentNumberGenerator);

        try {
            documentNumberGeneratorDTO.setNextNumber(this.documentNumberGeneratorService.getNextNumber(documentNumberGenerator));
        } catch (StringIndexOutOfBoundsException e) {
            documentNumberGeneratorDTO.setNextNumber("0");
        }

        return documentNumberGeneratorDTO;
    }
}
