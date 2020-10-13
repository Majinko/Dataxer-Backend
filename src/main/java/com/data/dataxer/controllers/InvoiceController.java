package com.data.dataxer.controllers;

import com.data.dataxer.filters.Filter;
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
            @RequestParam(value = "sort", defaultValue = "id") String sortColumn,
            @RequestBody(required = false) Filter filter
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortColumn)));

        return ResponseEntity.ok(invoiceService.paginate(pageable, filter).map(invoiceMapper::invoiceToInvoiceDTOSimple));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceMapper.invoiceToInvoiceDTO(this.invoiceService.getById(id)));
    }

    @RequestMapping(value = "/getAllByClient", method = RequestMethod.GET)
    public ResponseEntity<Page<InvoiceDTO>> getByClient(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "contactId") Long contactId,
            @RequestParam(value = "sort", defaultValue = "id") String sortColumn
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortColumn)));

        return ResponseEntity.ok(this.invoiceService.getByClient(pageable, contactId).map(this.invoiceMapper::invoiceToInvoiceDTOSimple));
    }

    @RequestMapping(value = "/duplicate/{id}", method = RequestMethod.POST)
    public ResponseEntity<InvoiceDTO> duplicate(@PathVariable Long id) {
        return ResponseEntity.ok(this.invoiceMapper.invoiceToInvoiceDTO(this.invoiceService.duplicate(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.invoiceService.destroy(id);
    }

    @GetMapping("/pdf/{id}")
    public void pdf(@PathVariable Long id) {

    }
}
