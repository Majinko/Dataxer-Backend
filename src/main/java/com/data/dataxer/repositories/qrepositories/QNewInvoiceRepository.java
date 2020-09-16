package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.NewInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QNewInvoiceRepository {

    Optional<NewInvoice> getById(Long id, List<Long> companyIds);

    Optional<NewInvoice> getByIdSimple(Long id, List<Long> companyIds);

    Page<NewInvoice> paginate(Pageable pageable, Filter[] filters, List<Long> companyIds);

}
