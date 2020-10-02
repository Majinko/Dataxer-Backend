package com.data.dataxer.controllers;

import com.data.dataxer.mappers.InvoiceMapper;
import com.data.dataxer.models.dto.InvoiceDTO;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.services.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceMapper invoiceMapper;

    public InvoiceController(InvoiceService invoiceService, InvoiceMapper invoiceMapper) {
        this.invoiceService = invoiceService;
        this.invoiceMapper = invoiceMapper;
    }

    @PostMapping("/store")
    public void store(@RequestBody InvoiceDTO invoiceDTO) {
        this.invoiceService.store(invoiceMapper.invoiceDTOtoInvoice(invoiceDTO));
    }

    @RequestMapping(value = "/storeTaxDocument", method = RequestMethod.POST)
    public void storeTaxDocument(
            @RequestParam(value = "id") Long id,
            @RequestBody InvoiceDTO invoiceDTO) {
        this.invoiceService.storeTaxDocument(this.invoiceMapper.invoiceDTOtoInvoice(invoiceDTO), id);
    }

    @RequestMapping(value = "/storeSummaryInvoice", method = RequestMethod.POST)
    public void storeTaxDocument(
            @RequestParam(value = "id1") Long taxDocumentId,
            @RequestParam(value = "id2") Long proformaId,
            @RequestBody InvoiceDTO invoiceDTO) {
        this.invoiceService.storeSummaryInvoice(this.invoiceMapper.invoiceDTOtoInvoice(invoiceDTO), taxDocumentId, proformaId);
    }

    @PostMapping("/update")
    public void update(@RequestBody InvoiceDTO invoiceDTO) {
        this.invoiceService.update(invoiceMapper.invoiceDTOtoInvoice(invoiceDTO));
    }

    @RequestMapping(value = "/changeState", method = RequestMethod.PUT)
    public void changeState(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "documentState") DocumentState newState
    ) {
        this.invoiceService.changeState(id, newState);
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<InvoiceDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+invoice.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(invoiceService.paginate(pageable, rqlFilter, sortExpression).map(invoiceMapper::invoiceToInvoiceDTOSimple));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceMapper.invoiceToInvoiceDTO(this.invoiceService.getById(id)));
    }

    @RequestMapping(value = "/duplicate/{id}", method = RequestMethod.POST)
    public ResponseEntity<InvoiceDTO> duplicate(@PathVariable Long id) {
        return ResponseEntity.ok(this.invoiceMapper.invoiceToInvoiceDTO(this.invoiceService.duplicate(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.invoiceService.destroy(id);
    }

    @GetMapping("/tax-invoice/{id}")
    public ResponseEntity<InvoiceDTO> getTaxDocument(@PathVariable Long id) {
        return ResponseEntity.ok(this.invoiceMapper.invoiceToInvoiceDTO(this.invoiceService.generateTaxDocument(id)));
    }

    @GetMapping("/summary-invoice/{id}")
    public ResponseEntity<InvoiceDTO> getSummaryInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(this.invoiceMapper.invoiceToInvoiceDTO(this.invoiceService.generateSummaryInvoice(id)));
    }

    @GetMapping("/pdf/{id}")
    public void pdf(@PathVariable Long id) {

    }
}
