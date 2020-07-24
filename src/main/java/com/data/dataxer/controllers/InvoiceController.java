package com.data.dataxer.controllers;

import com.data.dataxer.mappers.InvoiceMapper;
import com.data.dataxer.models.dto.InvoiceDTO;
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

    @PostMapping("/changeState")
    public void changeState(@RequestBody InvoiceDTO invoiceDTO) {
        this.invoiceService.changeState(invoiceMapper.invoiceDTOtoInvoice(invoiceDTO));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<InvoiceDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(invoiceService.paginate(pageable).map(invoiceMapper::invoiceToInvoiceDTOSimple));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceMapper.invoiceToInvoiceDTO(this.invoiceService.getById(id)));
    }

    /**
     * docasna verzia loadu invoicov podla stavu
     */
    @RequestMapping(value = "/getAllByState", method = RequestMethod.GET)
    public ResponseEntity<Page<InvoiceDTO>> getByState(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "state", defaultValue = "approved") String state
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(this.invoiceService.getByState(pageable, state).map(invoiceMapper::invoiceToInvoiceDTOSimple));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.invoiceService.destroy(id);
    }

}
