package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    void store(Payment payment);

    void update(Payment payment);

    Page<Payment> paginate(Pageable pageable, Filter filter);

    Payment getById(Long id);

    void destroy(Long id);

}
