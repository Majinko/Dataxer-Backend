package com.data.dataxer.controllers;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.mappers.PaymentMapper;
import com.data.dataxer.models.dto.PaymentDTO;
import com.data.dataxer.services.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping("/store")
    public void store(@RequestBody PaymentDTO paymentDTO) {
        this.paymentService.store(this.paymentMapper.paymentDTOtoPayment(paymentDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody PaymentDTO paymentDTO) {
        this.paymentService.update(this.paymentMapper.paymentDTOtoPayment(paymentDTO));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<PaymentDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sortColumn,
            @RequestBody(required = false) Filter filter
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortColumn)));

        return ResponseEntity.ok(this.paymentService.paginate(pageable, filter).map(this.paymentMapper::paymentToPaymentDTOSimple));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.paymentService.destroy(id);
    }

}
