package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QDocumentNumberGeneratorRepository {

    Page<DocumentNumberGenerator> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds);

    Optional<DocumentNumberGenerator> getById(Long id, List<Long> companyIds);

    Optional<DocumentNumberGenerator> getByIdSimple(Long id, List<Long> companyIds);

    Optional<DocumentNumberGenerator> getDefaultByDocumentType(DocumentType documentType, List<Long> companyIds);

}
