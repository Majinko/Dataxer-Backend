package com.data.dataxer.controllers;

import com.data.dataxer.mappers.DocumentRelationMapper;
import com.data.dataxer.models.dto.DocumentRelationDTO;
import com.data.dataxer.services.DocumentRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relation")
@PreAuthorize("hasPermission(null, 'DocumentRelation', 'DocumentRelation')")
public class DocumentRelationController {
    private final DocumentRelationService documentRelationsService;
    private final DocumentRelationMapper documentRelationMapper;

    public DocumentRelationController(DocumentRelationService documentRelationsService, DocumentRelationMapper documentRelationMapper) {
        this.documentRelationsService = documentRelationsService;
        this.documentRelationMapper = documentRelationMapper;
    }

    @PostMapping("/store")
    public void store(@RequestParam Long documentId, @RequestParam Long relatedDocumentId) {
        this.documentRelationsService.store(documentId, relatedDocumentId);
    }

    @GetMapping("/getRelatedDocuments/{id}")
    public ResponseEntity<List<DocumentRelationDTO>> getRelatedDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(documentRelationMapper.documentsBaseToDocumentRelationDTOs(this.documentRelationsService.getRelatedDocuments(id)));
    }

    @GetMapping("/destroy/{documentId}/{relatedDocumentId}")
    public void destroy(@PathVariable Long documentId, @PathVariable Long relatedDocumentId) {
        this.documentRelationsService.destroy(documentId, relatedDocumentId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentRelationDTO>> search(@RequestParam Long documentId, @RequestParam(defaultValue = "") String queryString) {
        return ResponseEntity.ok(documentRelationMapper.documentsBaseToDocumentRelationDTOs(this.documentRelationsService.search(documentId, queryString)));
    }
}
