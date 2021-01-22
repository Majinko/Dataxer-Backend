package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QDocumentNumberGeneratorRepository {

    Page<DocumentNumberGenerator> paginate(Pageable pageable, Filter filter, List<Long> companyIds);

    Optional<DocumentNumberGenerator> getById(Long id, List<Long> companyIds);

    Optional<DocumentNumberGenerator> getByIdSimple(Long id, List<Long> companyIds);

    Optional<DocumentNumberGenerator> getByDocumentType(DocumentType documentType, List<Long> companyIds);

    DocumentNumberGenerator getDefaultByDocumentType(DocumentType documentType, List<Long> companyIds);

}
