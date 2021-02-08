package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.MailTemplates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QMailTemplatesRepository {

    Optional<MailTemplates> getById(Long id, List<Long> companyIds);

    Page<MailTemplates> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds);

    long updateByMailTemplates(MailTemplates mailTemplates, List<Long> companyIds);

}
