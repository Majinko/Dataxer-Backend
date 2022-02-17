package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface DocumentNumberGeneratorService {
    void store(DocumentNumberGenerator documentNumberGenerator);

    DocumentNumberGenerator update(DocumentNumberGenerator documentNumberGenerator);

    Page<DocumentNumberGenerator> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    DocumentNumberGenerator getById(Long id);

    DocumentNumberGenerator getByIdSimple(Long id);

    void destroy(Long id);

    String generateNextNumberByDocumentType(DocumentType documentType, LocalDate date, Long companyId);

    String getNextNumber(DocumentNumberGenerator documentNumberGenerator);

    List<DocumentNumberGenerator> getAll();
}
