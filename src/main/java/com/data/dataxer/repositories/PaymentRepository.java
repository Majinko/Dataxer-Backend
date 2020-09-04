package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Payment;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
