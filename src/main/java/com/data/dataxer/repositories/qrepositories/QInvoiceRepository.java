package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface QInvoiceRepository {
    Page<Invoice> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);

    Optional<Invoice> getById(Long id, Long appProfileId);

    Optional<Invoice> getById(Long id);

    Optional<Invoice> getByIdSimple(Long id, Long appProfileId);

    List<Invoice> getAllInvoicesIdInAndType(List<Long> ids, DocumentType type, Long appProfileId);

    //last for DAILY period
    Invoice getLastInvoiceByDayAndMonthAndYear(List<DocumentType> types, LocalDate localDate, Long companyId, Long appProfileId);

    //last for monthly
    Invoice getLastInvoiceByMonthAndYear(List<DocumentType> types, LocalDate localDate, Long companyId, Long appProfileId);

    //last for quarter period
    Invoice getLastInvoiceByQuarterAndYear(List<DocumentType> types, LocalDate localDate, Long companyId, Long appProfileId);

    //last for half-year period
    Invoice getLastInvoiceByHalfAndYear(List<DocumentType> types, LocalDate localDate, Long companyId, Long appProfileId);

    //last for year period
    Invoice getLastInvoiceByYear(List<DocumentType> types, LocalDate localDate, Long companyId, Long appProfileId);
}
