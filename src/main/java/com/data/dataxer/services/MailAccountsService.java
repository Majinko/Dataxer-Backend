package com.data.dataxer.services;

import com.data.dataxer.models.domain.MailAccounts;
import com.data.dataxer.models.dto.EmailMessage;
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

    void sendEmail(String emailSubject, String emailContent, List<String> emails);

    void sendEmail(EmailMessage emailMessage, List<String> emails);

    void sendEmailWithAttachments(String subject, String content, List<Long> recipientIds, Long templateId, List<String> fileNames, Long companyId);
}
