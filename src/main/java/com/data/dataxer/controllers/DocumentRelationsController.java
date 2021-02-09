package com.data.dataxer.controllers;

import com.data.dataxer.mappers.DocumentRelationMapper;
import com.data.dataxer.models.dto.DocumentRelationDTO;
import com.data.dataxer.services.DocumentRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relation")
public class DocumentRelationsController {
    private final DocumentRelationService documentRelationsService;
    private final DocumentRelationMapper documentRelationMapper;

    public DocumentRelationsController(DocumentRelationService documentRelationsService, DocumentRelationMapper documentRelationMapper) {
        this.documentRelationsService = documentRelationsService;
        this.documentRelationMapper = documentRelationMapper;
    }

    @PostMapping("/store")
    public ResponseEntity<DocumentRelationDTO> store(@RequestParam Long documentId, @RequestParam Long relatedDocumentId) {
        return ResponseEntity.ok(documentRelationMapper.documentToDocumentRelationDTO(this.documentRelationsService.store(documentId, relatedDocumentId)));
    }

    @GetMapping("/getRelatedDocuments/{id}")
    public ResponseEntity<List<DocumentRelationDTO>> getRelatedDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(documentRelationMapper.documentsBaseToDocumentRelationDTOs(this.documentRelationsService.getRelatedDocuments(id)));
    }

    @GetMapping("/destroy/{documentId}/{relatedDocumentId}")
    public void destroy(@PathVariable Long documentId, @PathVariable Long relatedDocumentId) {
        this.documentRelationsService.destroy(documentId, relatedDocumentId);
    }

    @GetMapping("/search/{queryString}")
    public ResponseEntity<List<DocumentRelationDTO>> search(@PathVariable String queryString) {
        return ResponseEntity.ok(documentRelationMapper.documentsBaseToDocumentRelationDTOs(this.documentRelationsService.search(queryString)));
    }
}
