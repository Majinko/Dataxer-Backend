package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    void store(Payment payment);

    void update(Payment payment);

    Page<Payment> paginate(Pageable pageable, Filter filter);

    Payment getById(Long id);

    void destroy(Long id);

    BigDecimal getRestToPay(Long documentId, DocumentType documentType);

    List<Payment> getWithoutTaxDocumentCreatedByDocumentId(Long documentId);

}
