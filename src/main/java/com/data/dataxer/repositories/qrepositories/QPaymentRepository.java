package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QPaymentRepository {

    Page<Payment> paginate(Pageable pageable, Filter filter, List<Long> companyIds);

    Optional<Payment> getById(Long id, List<Long> companyIds);

}
