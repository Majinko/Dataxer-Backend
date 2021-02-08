package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QDocumentNumberGeneratorRepository {

    Page<DocumentNumberGenerator> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId, Boolean disableFilter);

    Optional<DocumentNumberGenerator> getById(Long id, Long companyId, Boolean disableFilter);

    Optional<DocumentNumberGenerator> getByIdSimple(Long id, Long companyId, Boolean disableFilter);

    DocumentNumberGenerator getDefaultByDocumentType(DocumentType documentType, Long companyId, Boolean disableFilter);

}
