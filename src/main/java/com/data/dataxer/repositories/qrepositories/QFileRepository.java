package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QFileRepository {

    Optional<File> getByNameAndCompanyIds(String fileName, List<Long> companyIds);

    Page<File> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds);
}
