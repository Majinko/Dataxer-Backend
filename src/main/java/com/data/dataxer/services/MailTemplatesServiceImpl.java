package com.data.dataxer.services;

import com.data.dataxer.models.domain.MailTemplate;
import com.data.dataxer.models.enums.MailTemplateType;
import com.data.dataxer.repositories.MailTemplatesRepository;
import com.data.dataxer.repositories.qrepositories.QMailTemplatesRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MailTemplatesServiceImpl implements MailTemplatesService {
    private final MailTemplatesRepository mailTemplatesRepository;
    private final QMailTemplatesRepository qMailTemplatesRepository;

    public MailTemplatesServiceImpl(MailTemplatesRepository mailTemplatesRepository, QMailTemplatesRepository qMailTemplatesRepository) {
        this.mailTemplatesRepository = mailTemplatesRepository;
        this.qMailTemplatesRepository = qMailTemplatesRepository;
    }

    @Override
    @Transactional
    public void store(MailTemplate mailTemplates) {
        this.mailTemplatesRepository.save(mailTemplates);
    }

    @Override
    @Transactional
    public void update(MailTemplate mailTemplates) {
        if (this.qMailTemplatesRepository.updateByMailTemplates(mailTemplates, SecurityUtils.defaultProfileId()) != 1) {
            throw new RuntimeException("Mail template update failed");
        }
    }

    @Override
    public MailTemplate getById(Long id) {
        return this.qMailTemplatesRepository.getById(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Mail templates not found"));
    }

    @Override
    public Page<MailTemplate> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qMailTemplatesRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.defaultProfileId());
    }

    @Override
    public void destroy(Long id) {
        this.mailTemplatesRepository.delete(this.getById(id));
    }

    @Override
    public List<MailTemplate> getAll() {
        return this.mailTemplatesRepository.findAllByAppProfileId(SecurityUtils.defaultProfileId());
    }

    @Override
    public List<MailTemplate> storeOrUpdateAll(List<MailTemplate> mailTemplates) {
        return (List<MailTemplate>) this.mailTemplatesRepository.saveAll(mailTemplates);
    }

    @Override
    public MailTemplate getByType(String type) {
        return this.mailTemplatesRepository.findByType(MailTemplateType.valueOf(type), SecurityUtils.defaultProfileId());
    }
}
