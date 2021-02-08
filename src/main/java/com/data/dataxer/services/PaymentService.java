package com.data.dataxer.services;

import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.print.Doc;
import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    Payment store(Payment payment);

    void update(Payment payment);

    Page<Payment> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter);

    Payment getById(Long id, Boolean disableFilter);

    void destroy(Long id);

    BigDecimal getRestToPay(Long documentId, DocumentType documentType);

    List<Payment> getDocumentPayments(Long id, DocumentType type);
}
