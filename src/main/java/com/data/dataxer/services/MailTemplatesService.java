package com.data.dataxer.services;

import com.data.dataxer.models.domain.MailTemplates;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MailTemplatesService {

    void store(MailTemplates mailTemplates);

    void update(MailTemplates mailTemplates);

    MailTemplates getById(Long id);

    Page<MailTemplates> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    void destroy(Long id);

}
