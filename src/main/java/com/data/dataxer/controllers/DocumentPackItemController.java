package com.data.dataxer.controllers;

import com.data.dataxer.services.DocumentPackItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documentPackItem")
@PreAuthorize("hasPermission(null, 'Document', 'Document')")
public class DocumentPackItemController {
    @Autowired
    private DocumentPackItemService documentPackItemService;

    @GetMapping(value = "/deleteItemFromDocument")
    public void deleteItemFromDocument(
            @RequestParam(value = "documentId", required = true) Long documentId,
            @RequestParam(value = "packId", required = true) Long packId,
            @RequestParam(value = "packItemId", required = true) Long packItemId
    ) {
        this.documentPackItemService.deleteItemFromDocument(documentId, packId, packItemId);
    }
}
