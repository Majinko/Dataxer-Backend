package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.DocumentNumberGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentNumberGeneratorService {

    void store(DocumentNumberGenerator documentNumberGenerator);

    void update(DocumentNumberGenerator documentNumberGenerator);

    Page<DocumentNumberGenerator> paginate(Pageable pageable, Filter filter);

    DocumentNumberGenerator getById(Long id);

    DocumentNumberGenerator getByIdSimple(Long id);

    void destroy(Long id);

    String generateNextNumberByDocumentType(String documentType);
}
