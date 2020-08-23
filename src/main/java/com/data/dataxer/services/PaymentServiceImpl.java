package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.repositories.PaymentRepository;
import com.data.dataxer.repositories.qrepositories.QPaymentRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final QPaymentRepository qPaymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, QPaymentRepository qPaymentRepository) {
        this.paymentRepository = paymentRepository;
        this.qPaymentRepository = qPaymentRepository;
    }

    @Override
    public void store(Payment payment) {
        this.paymentRepository.save(payment);
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
}