package com.data.dataxer.services;

import com.data.dataxer.models.domain.MailAccounts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MailAccountsService {
    void store(MailAccounts mailAccounts);

    void update(MailAccounts mailAccounts);

    MailAccounts getById(Long id);

    Page<MailAccounts> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    void destroy(Long id);

    void activate(Long id);

    void deactivate(Long id);

    void sendEmail(String emailSubject, String emailContent, List<Long> contactIds, Long companyId, Long templateId);

    void sendEmailWithAttachments(String subject, String content, List<Long> recipientIds, Long templateId, List<String> fileNames, Long companyId);
}
