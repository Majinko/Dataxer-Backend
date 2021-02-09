package com.data.dataxer.controllers;

import com.data.dataxer.models.dto.DocumentRelationDTO;
import com.data.dataxer.services.DocumentRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relation")
public class DocumentRelationsController {

    private final DocumentRelationService documentRelationsService;

    public DocumentRelationsController(DocumentRelationService documentRelationsService) {
        this.documentRelationsService = documentRelationsService;
    }

    @PostMapping("/store")
    public void store(@RequestParam Long documentId, @RequestParam Long relatedDocumentId) {
        this.documentRelationsService.store(documentId, relatedDocumentId);
    }

    @GetMapping("/getRelatedDocuments/{id}")
    public ResponseEntity<List<DocumentRelationDTO>> getRelatedDocuments(@PathVariable Long id) {
        return ResponseEntity.ok((this.documentRelationsService.getRelatedDocuments(id)));
    }

    @GetMapping("/destroy/{documentId}/{relatedDocumentId}")
    public void destroy(@PathVariable Long documentId, @PathVariable Long relatedDocumentId) {
        this.documentRelationsService.destroy(documentId, relatedDocumentId);
    }
}
