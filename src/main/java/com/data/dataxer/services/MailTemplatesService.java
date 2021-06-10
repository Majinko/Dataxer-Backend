package com.data.dataxer.services;

import com.data.dataxer.models.domain.MailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MailTemplatesService {

    void store(MailTemplate mailTemplates);

    void update(MailTemplate mailTemplates);

    MailTemplate getById(Long id);

    Page<MailTemplate> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    void destroy(Long id);

    List<MailTemplate> getAll();

    void storeOrUpdateAll(List<MailTemplate> mailTemplatesDTOtoMailTemplates);
}
