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
import java.util.List;

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
    public ResponseEntity<PaymentDTO> store(@RequestBody PaymentDTO paymentDTO) {
        return ResponseEntity.ok(this.paymentMapper.paymentToPaymentDTO(this.paymentService.store(paymentMapper.paymentDTOtoPayment(paymentDTO))));
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

        return ResponseEntity.ok(this.paymentService.paginate(pageable, rqlFilter, sortExpression, false).map(this.paymentMapper::paymentToPaymentDTOSimple));
    }

    @GetMapping("/restToPay/{id}/{type}")
    public BigDecimal getRestToPay(@PathVariable Long id, @PathVariable String type) {
        return this.paymentService.getRestToPay(id, DocumentType.valueOf(type));
    }

    @GetMapping("/document-payments/{id}/{type}")
    public ResponseEntity<List<PaymentDTO>> getDocumentPayments(@PathVariable Long id, @PathVariable String type) {
        return ResponseEntity.ok(this.paymentMapper.paymentsToPaymentDTOs(paymentService.getDocumentPayments(id, DocumentType.valueOf(type))));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.paymentService.destroy(id);
    }
}
