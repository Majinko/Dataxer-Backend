package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QFileRepository {
    Optional<File> getByNameAndProfileId(String fileName, Long appProfileId);

    Page<File> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);
}
