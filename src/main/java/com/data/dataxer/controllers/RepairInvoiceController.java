package com.data.dataxer.controllers;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.InvoiceRepository;
import com.data.dataxer.repositories.PaymentRepository;
import com.data.dataxer.repositories.qrepositories.QPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/invoice/repair")
public class RepairInvoiceController {
    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    QPaymentRepository qPaymentRepository;

    @GetMapping("")
    public String repair() {
        List<Invoice> invoices = (List<Invoice>) invoiceRepository.findAll();

        invoices.forEach(invoice -> {
            if (invoice.getDocumentType().equals(DocumentType.TAX_DOCUMENT)) {
                invoice.setState(DocumentState.PAYED);
            } else {
                BigDecimal payedTotalPrice = this.qPaymentRepository.getPayedTotalPrice(invoice.getId(), invoice.getDocumentType());

                boolean isPayed = invoice.getTotalPrice().subtract(payedTotalPrice).setScale(2, RoundingMode.HALF_UP).compareTo(BigDecimal.ZERO) == 0;

                if (isPayed || invoice.getPaymentDate() != null) {
                    invoice.setState(DocumentState.PAYED);
                }
            }

            invoiceRepository.save(invoice);
        });

        return "test";
    }
}
