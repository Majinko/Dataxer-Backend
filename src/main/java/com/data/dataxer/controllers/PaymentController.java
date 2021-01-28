package com.data.dataxer.controllers;

import com.data.dataxer.mappers.PaymentMapper;
import com.data.dataxer.models.dto.PaymentDTO;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.services.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+payment.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(this.paymentService.paginate(pageable, rqlFilter, sortExpression).map(this.paymentMapper::paymentToPaymentDTOSimple));
    }

    @RequestMapping(value = "/restToPay", method = RequestMethod.GET)
    public BigDecimal getRestToPay(
            @RequestParam(value = "id") Long documentId,
            @RequestParam(value = "documentType") DocumentType documentType
    ) {
        return this.paymentService.getRestToPay(documentId, documentType);
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.paymentService.destroy(id);
    }

}
