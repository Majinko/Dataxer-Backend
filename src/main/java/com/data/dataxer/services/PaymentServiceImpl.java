package com.data.dataxer.services;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.InvoiceRepository;
import com.data.dataxer.repositories.PaymentRepository;
import com.data.dataxer.repositories.qrepositories.QPaymentRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final QPaymentRepository qPaymentRepository;
    private final InvoiceRepository invoiceRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, QPaymentRepository qPaymentRepository, InvoiceRepository invoiceRepository) {
        this.paymentRepository = paymentRepository;
        this.qPaymentRepository = qPaymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public Payment store(Payment payment) {
        this.paymentRepository.save(payment);

        if (this.documentIsPayed(payment)) {
            if (payment.getDocumentType().equals(DocumentType.INVOICE) || payment.getDocumentType().equals(DocumentType.PROFORMA)) {
                Invoice invoice = this.invoiceRepository.findByIdAndCompanyIdIn(payment.getDocumentId(), SecurityUtils.companyIds());
                invoice.setPaymentDate(payment.getPayedDate());
                invoiceRepository.save(invoice);
            }
        }

        return payment;
    }

    @Override
    public void update(Payment payment) {
        this.paymentRepository.save(payment);
    }

    @Override
    public Page<Payment> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter) {
        return this.qPaymentRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyId(), disableFilter);
    }

    @Override
    public Payment getById(Long id, Boolean disableFilter) {
        return this.qPaymentRepository
                .getById(id, SecurityUtils.companyId(), disableFilter)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    public void destroy(Long id) {
        Payment payment = this.getById(id, false);
        Invoice invoice = this.invoiceRepository.findByIdAndCompanyIdIn(payment.getDocumentId(), SecurityUtils.companyIds());

        if (this.documentIsPayed(payment)) {
            invoice.setPaymentDate(null);
            invoiceRepository.save(invoice);
        }

        this.paymentRepository.delete(payment);
    }

    @Override
    public BigDecimal getRestToPay(Long documentId, DocumentType documentType) {
        BigDecimal documentTotalPrice = this.qPaymentRepository.getDocumentTotalPrice(documentId, documentType);
        BigDecimal payedTotalPrice = this.qPaymentRepository.getPayedTotalPrice(documentId);

        return documentTotalPrice.subtract(payedTotalPrice);
    }

    @Override
    public List<Payment> getDocumentPayments(Long id, DocumentType type) {
        return this.paymentRepository.findAllByDocumentIdAndDocumentType(id, type);
    }

    private boolean documentIsPayed(Payment payment) {
        return this.getRestToPay(payment.getDocumentId(), payment.getDocumentType()).compareTo(BigDecimal.ZERO) == 0;
    }
}
