package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.MailTemplates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QMailTemplatesRepository {

    Optional<MailTemplates> getById(Long id, Long companyId, Boolean disableFilter);

    Page<MailTemplates> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId, Boolean disableFilter);

    long updateByMailTemplates(MailTemplates mailTemplates, Long companyId, Boolean disableFilter);

}
