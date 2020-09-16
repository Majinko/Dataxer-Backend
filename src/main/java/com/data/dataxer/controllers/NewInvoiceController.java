package com.data.dataxer.controllers;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.mappers.NewInvoiceMapper;
import com.data.dataxer.models.dto.InvoiceDTO;
import com.data.dataxer.services.NewInvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/new_invoice")
public class NewInvoiceController {

    private final NewInvoiceService newInvoiceService;
    private final NewInvoiceMapper newInvoiceMapper;

    public NewInvoiceController(NewInvoiceService newInvoiceService, NewInvoiceMapper newInvoiceMapper) {
        this.newInvoiceService = newInvoiceService;
        this.newInvoiceMapper = newInvoiceMapper;
    }

    @PostMapping("/store")
    public void store(@RequestBody InvoiceDTO invoiceDTO) {
        this.newInvoiceService.store(this.newInvoiceMapper.invoiceDTOToNewInvoice(invoiceDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody InvoiceDTO invoiceDTO) {
        this.newInvoiceService.update(this.newInvoiceMapper.invoiceDTOToNewInvoice(invoiceDTO));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<InvoiceDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sortColumn,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(required = false) Filter[] filter
    ) {
        Pageable pageable;
        if (order.equals("desc")) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortColumn)));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortColumn)));
        }
        return ResponseEntity.ok(this.newInvoiceService.paginate(pageable, filter).map(newInvoiceMapper::newInvoiceToInvoiceDTOSimple));
    }

}
