package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QPaymentRepositoryImpl implements QPaymentRepository {
    @Override
    public Page<Payment> paginate(Pageable pageable, Filter filter, List<Long> companyIds) {
        return null;
    }

    @Override
    public Optional<Payment> getById(Long id, List<Long> companyIds) {
        return Optional.empty();
    }

    @Override
    public Optional<Payment> getByIdSimple(Long id, List<Long> companyIds) {
        return Optional.empty();
    }
}
