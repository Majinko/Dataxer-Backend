package com.data.dataxer.services;

import com.data.dataxer.models.domain.MailTemplates;
import com.data.dataxer.repositories.MailTemplatesRepository;
import com.data.dataxer.repositories.qrepositories.QMailTemplatesRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void store(MailTemplates mailTemplates) {
        this.mailTemplatesRepository.save(mailTemplates);
    }

    @Override
    @Transactional
    public void update(MailTemplates mailTemplates) {
        if (this.qMailTemplatesRepository.updateByMailTemplates(mailTemplates, SecurityUtils.companyId()) != 1) {
            throw new RuntimeException("Mail template update failed");
        }
    }

    @Override
    public MailTemplates getById(Long id) {
        return this.qMailTemplatesRepository.getById(id, SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Mail templates not found"));
    }

    @Override
    public Page<MailTemplates> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qMailTemplatesRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyId());
    }

    @Override
    public void destroy(Long id) {
        this.mailTemplatesRepository.delete(this.getById(id));
    }
}
