package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.models.enums.DocumentState;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.PaymentRepository;
import com.data.dataxer.repositories.qrepositories.QPaymentRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final QPaymentRepository qPaymentRepository;
    private final InvoiceService invoiceService;
    private final PriceOfferService priceOfferService;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            QPaymentRepository qPaymentRepository,
            InvoiceService invoiceService,
            PriceOfferService priceOfferService
    ) {
        this.paymentRepository = paymentRepository;
        this.qPaymentRepository = qPaymentRepository;
        this.invoiceService = invoiceService;
        this.priceOfferService = priceOfferService;
    }

    @Override
    public void store(Payment payment) {
        this.paymentRepository.save(payment);
        if (this.documentIsPayed(payment)) {
            if (payment.getDocumentType().equals(DocumentType.INVOICE)) {
                this.invoiceService.changeState(payment.getDocumentId(), DocumentState.PAYED);
            }
            if (payment.getDocumentType().equals(DocumentType.PRICE_OFFER)) {
                //not implemented now
                //this.priceOfferService.setPayed(payment.getDocumentId());
            }
        }
    }

    @Override
    public void update(Payment payment) {
        this.paymentRepository.save(payment);
    }

    @Override
    public Page<Payment> paginate(Pageable pageable, Filter filter) {
        return this.qPaymentRepository.paginate(pageable, filter, SecurityUtils.companyIds());
    }

    @Override
    public Payment getById(Long id) {
        return this.qPaymentRepository
                .getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    public void destroy(Long id) {
        this.paymentRepository.delete(this.getById(id));
    }

    @Override
    public BigDecimal getRestToPay(Long documentId, DocumentType documentType) {
        BigDecimal documentTotalPrice = this.qPaymentRepository.getDocumentTotalPrice(documentId, documentType);
        BigDecimal payedTotalPrice = this.qPaymentRepository.getPayedTotalPrice(documentId);

        return documentTotalPrice.subtract(payedTotalPrice);
    }

    private boolean documentIsPayed(Payment payment) {
        return this.getRestToPay(payment.getDocumentId(), payment.getDocumentType()).compareTo(BigDecimal.ZERO) == 0;
    }
}
