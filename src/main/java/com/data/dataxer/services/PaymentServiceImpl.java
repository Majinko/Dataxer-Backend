package com.data.dataxer.services;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.CostRepository;
import com.data.dataxer.repositories.InvoiceRepository;
import com.data.dataxer.repositories.PaymentRepository;
import com.data.dataxer.repositories.qrepositories.QPaymentRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static java.math.BigDecimal.ROUND_HALF_EVEN;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final QPaymentRepository qPaymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final CostRepository costRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, QPaymentRepository qPaymentRepository, InvoiceRepository invoiceRepository, CostRepository costRepository) {
        this.paymentRepository = paymentRepository;
        this.qPaymentRepository = qPaymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.costRepository = costRepository;
    }

    @Override
    public Payment store(Payment payment) {
        this.paymentRepository.save(payment);

        if (this.documentIsPayed(payment)) {
            if (payment.getDocumentType().equals(DocumentType.COST)) {
                this.setCostPayment(payment.getDocumentId(), payment.getPayedDate(), DocumentState.PAYED); // todo spravit ako ma faktura
            } else {
                this.setInvoicePayment(payment.getDocumentId(), payment.getPayedDate());
            }
        }

        return payment;
    }

    @Override
    public void update(Payment payment) {
        this.paymentRepository.save(payment);
    }

    @Override
    public Page<Payment> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qPaymentRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyId());
    }

    @Override
    public Payment getById(Long id) {
        return this.qPaymentRepository
                .getById(id, SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    public void destroy(Long id) {
        Payment payment = this.getById(id);

        if (this.documentIsPayed(payment)) {
            if (payment.getDocumentType().equals(DocumentType.COST)) {
                this.setCostPayment(payment.getDocumentId(), null, DocumentState.UNPAID);
            } else {
                this.setInvoicePayment(payment.getDocumentId(), null);
            }
        }

        this.paymentRepository.delete(payment);
    }

    private void setCostPayment(Long costId, LocalDate date, DocumentState documentState) {
        Cost cost = this.costRepository.findByIdAndCompanyId(costId, SecurityUtils.companyId());
        cost.setPaymentDate(date);
        cost.setState(documentState);

        costRepository.save(cost);
    }

    private void setInvoicePayment(Long invoiceId, LocalDate date) {
        Invoice invoice = this.invoiceRepository.findByIdAndCompanyId(invoiceId, SecurityUtils.companyId());
        invoice.setPaymentDate(date);
        invoice.setState(DocumentState.PAYED);
        invoiceRepository.save(invoice);
    }

    @Override
    public BigDecimal getRestToPay(Long documentId, DocumentType documentType) {
        BigDecimal documentTotalPrice = this.qPaymentRepository.getDocumentTotalPrice(documentId, documentType);
        BigDecimal payedTotalPrice = this.qPaymentRepository.getPayedTotalPrice(documentId);

        return documentTotalPrice.subtract(payedTotalPrice).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public List<Payment> getDocumentPayments(Long id, DocumentType type) {
        return this.paymentRepository.findAllByDocumentIdAndDocumentType(id, type);
    }

    private boolean documentIsPayed(Payment payment) {
        return this.getRestToPay(payment.getDocumentId(), payment.getDocumentType()).compareTo(BigDecimal.ZERO) == 0;
    }
}
