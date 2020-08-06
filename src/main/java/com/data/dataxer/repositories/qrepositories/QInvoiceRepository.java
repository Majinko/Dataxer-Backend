package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentState;
import com.google.api.client.json.Json;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QInvoiceRepository {

    Page<Invoice> paginate(Pageable pageable, Filter filter, List<Long> companyIds);

    Optional<Invoice> getById(Long id, List<Long> companyIds);

    Optional<Invoice> getByIdSimple(Long id, List<Long> companyIds);

    Page<Invoice> getByClient(Pageable pageable, Long contactId, List<Long> companyIds);
}
