package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Payment;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface QPaymentRepository {

    Page<Payment> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds);

    Optional<Payment> getById(Long id, List<Long> companyIds);

    BigDecimal getDocumentTotalPrice(Long id, DocumentType documentType);

    BigDecimal getPayedTotalPrice(Long id);
}
