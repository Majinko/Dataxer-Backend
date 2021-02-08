package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentNumberGeneratorService {

    void store(DocumentNumberGenerator documentNumberGenerator);

    DocumentNumberGenerator update(DocumentNumberGenerator documentNumberGenerator);

    Page<DocumentNumberGenerator> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter);

    DocumentNumberGenerator getById(Long id, Boolean disableFilter);

    DocumentNumberGenerator getByIdSimple(Long id, Boolean disableFilter);

    void destroy(Long id);

    String generateNextNumberByDocumentType(DocumentType documentType, boolean storeGenerated, Boolean disableFilter);

    String generateNextNumberByDocumentTypeFromString(String type, Boolean disableFilter);

    String getNextNumber(DocumentNumberGenerator documentNumberGenerator);

    void resetGenerationByType(DocumentType documentType, Boolean disableFilter);

    void resetGenerationById(Long id, Boolean disableFilter);

    String generateNextNumberByDocumentId(Long id, boolean storeGenerated, Boolean disableFilter);
}
