package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentNumberGeneratorService {

    void storeOrUpdate(DocumentNumberGenerator documentNumberGenerator);

    DocumentNumberGenerator update(DocumentNumberGenerator documentNumberGenerator);

    Page<DocumentNumberGenerator> paginate(Pageable pageable, Filter filter);

    DocumentNumberGenerator getById(Long id);

    DocumentNumberGenerator getByIdSimple(Long id);

    void destroy(Long id);

    String generateNextNumberByDocumentType(DocumentType documentType);

    String getNextNumber(DocumentNumberGenerator documentNumberGenerator);

    void resetGenerationByType(DocumentType documentType);

    void resetGenerationById(Long id);
}
