package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.MailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface QMailTemplatesRepository {

    Optional<MailTemplate> getById(Long id, Long companyId);

    Page<MailTemplate> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId);

    long updateByMailTemplates(MailTemplate mailTemplates, Long companyId);

}
