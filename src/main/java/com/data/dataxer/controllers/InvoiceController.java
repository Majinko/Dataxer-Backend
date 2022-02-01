package com.data.dataxer.controllers;

import com.data.dataxer.mappers.InvoiceMapper;
import com.data.dataxer.models.dto.InvoiceDTO;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.services.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/invoice")
@PreAuthorize("hasPermission(null, 'Document', 'Document')")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoiceMapper invoiceMapper;

    @PostMapping("/store")
    public void store(@RequestBody InvoiceDTO invoiceDTO) {
        this.invoiceService.store(invoiceMapper.invoiceDTOtoInvoiceWithCompany(invoiceDTO));
    }

    @PostMapping("/store/{oldInvoiceId}")
    public void store(@RequestBody InvoiceDTO invoiceDTO, @PathVariable Long oldInvoiceId) {
        this.invoiceService.store(invoiceMapper.invoiceDTOtoInvoiceWithCompany(invoiceDTO), oldInvoiceId);
    }

    @RequestMapping(value = "/storeSummaryInvoice", method = RequestMethod.POST)
    public void storeTaxDocument(@RequestParam(value = "id1") Long taxDocumentId, @RequestParam(value = "id2") Long proformaId, @RequestBody InvoiceDTO invoiceDTO) {
        this.invoiceService.storeSummaryInvoice(this.invoiceMapper.invoiceDTOtoInvoiceWithCompany(invoiceDTO), taxDocumentId, proformaId);
    }

    @PostMapping("/update")
    public void update(@RequestBody InvoiceDTO invoiceDTO) {
        this.invoiceService.update(invoiceMapper.invoiceDTOtoInvoiceWithCompany(invoiceDTO));
    }

    @RequestMapping(value = "/changeState", method = RequestMethod.PUT)
    public void changeState(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "documentState") DocumentState newState,
            @RequestParam(value = "payedDate") LocalDate payedDate
    ) {
        this.invoiceService.makePay(id, payedDate);
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<InvoiceDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(-invoice.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(invoiceService.paginate(pageable, rqlFilter, sortExpression).map(invoiceMapper::invoiceToInvoiceDTOSimple));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceMapper.invoiceToInvoiceDTOWithCompany(this.invoiceService.getById(id)));
    }

    @GetMapping(value = "/duplicate/{id}")
    public ResponseEntity<InvoiceDTO> duplicate(@PathVariable Long id) {
        return ResponseEntity.ok(this.invoiceMapper.invoiceToInvoiceDTOWithCompany(this.invoiceService.duplicate(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.invoiceService.destroy(id);
    }

    @GetMapping("/tax-invoice/{id}")
    public ResponseEntity<InvoiceDTO> getTaxDocument(@PathVariable Long id) {
        return ResponseEntity.ok(this.invoiceMapper.invoiceToInvoiceDTOWithCompany(this.invoiceService.generateTaxDocument(id)));
    }

    @GetMapping("/summary-invoice/{id}")
    public ResponseEntity<InvoiceDTO> getSummaryInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(this.invoiceMapper.invoiceToInvoiceDTOWithCompany(this.invoiceService.generateSummaryInvoice(id, "taxDocument")));
    }

    @GetMapping("/summary-invoice/{type}/{id}")
    public ResponseEntity<InvoiceDTO> getSummaryInvoiceByType(@PathVariable String type, @PathVariable Long id) {
        return ResponseEntity.ok(this.invoiceMapper.invoiceToInvoiceDTOWithCompany(this.invoiceService.generateSummaryInvoice(id, type)));
    }

    @GetMapping("/all-related-invoices/{id}")
    public ResponseEntity<List<InvoiceDTO>> findAllByRelatedDocuments(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceMapper.invoicesToInvoicesDTOWithoutRelation(this.invoiceService.findAllByRelatedDocuments(id)));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<InvoiceDTO>> findAllByProject(
            @PathVariable Long projectId,
            @RequestParam(value = "companyIds", required = false) List<Long> companyIds
    ) {
        return ResponseEntity.ok(invoiceMapper.invoicesToInvoicesDTOWithoutRelation(this.invoiceService.findAllByProject(projectId, companyIds)));
    }
}
