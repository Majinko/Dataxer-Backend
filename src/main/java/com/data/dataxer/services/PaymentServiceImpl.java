package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class PaymentServiceImpl implements PaymentService {
    @Override
    public void store(Payment payment) {

    }

    @Override
    public void update(Payment payment) {

    }

    @Override
    public Page<Payment> paginate(Pageable pageable, Filter filter) {
        return null;
    }

    @Override
    public void destroy(Long id) {

    }
}
