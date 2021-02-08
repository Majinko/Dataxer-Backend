package com.data.dataxer.controllers;

import com.data.dataxer.mappers.DocumentRelationMapper;
import com.data.dataxer.mappers.InvoiceMapper;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.dto.DocumentRelationDTO;
import com.data.dataxer.models.dto.InvoiceDTO;
import com.data.dataxer.services.DocumentRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/relation")
public class DocumentRelationsController {

    private final DocumentRelationService documentRelationsService;
    private final InvoiceMapper invoiceMapper;
    private final DocumentRelationMapper documentRelationMapper;

    public DocumentRelationsController(DocumentRelationService documentRelationsService, InvoiceMapper invoiceMapper,
                                       DocumentRelationMapper documentRelationMapper) {
        this.documentRelationsService = documentRelationsService;
        this.invoiceMapper = invoiceMapper;
        this.documentRelationMapper = documentRelationMapper;
    }

    @PostMapping("/store")
    public void store(@RequestParam Long documentId, @RequestParam Long relatedDocumentId) {
        this.documentRelationsService.store(documentId, relatedDocumentId);
    }

    @GetMapping("/getDocumentRelations/{id}")
    public ResponseEntity<List<InvoiceDTO>> getAllRelationDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(this.mapListInvoiceToListInvoiceDTO(this.documentRelationsService.getAllRelatedDocuments(id)));
    }

    @GetMapping("/getRelatedDocuments/{id}")
    public ResponseEntity<List<DocumentRelationDTO>> getRelatedDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(this.documentRelationMapper.documentRelationToDocumentRelationDTO(this.documentRelationsService.getRelatedDocuments(id)));
    }

    private List<InvoiceDTO> mapListInvoiceToListInvoiceDTO(List<Invoice> invoices) {
        List<InvoiceDTO> invoiceDTOS = new ArrayList<>();
        for (Invoice invoice : invoices) {
            invoiceDTOS.add(this.invoiceMapper.invoiceToInvoiceDTO(invoice));
        }
        return invoiceDTOS;
    }

}
