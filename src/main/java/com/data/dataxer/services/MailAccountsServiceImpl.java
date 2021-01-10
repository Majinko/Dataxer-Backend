package com.data.dataxer.services;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.domain.MailAccounts;
import com.data.dataxer.models.enums.MailAccountState;
import com.data.dataxer.repositories.MailAccountsRepository;
import com.data.dataxer.repositories.qrepositories.QMailAccountsRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@Service
public class MailAccountsServiceImpl implements MailAccountsService {

    private final MailAccountsRepository mailAccountsRepository;
    private final QMailAccountsRepository qMailAccountsRepository;
    private final ContactService contactService;

    public MailAccountsServiceImpl(MailAccountsRepository mailAccountsRepository, QMailAccountsRepository qMailAccountsRepository,
                                   ContactService contactService) {
        this.mailAccountsRepository = mailAccountsRepository;
        this.qMailAccountsRepository = qMailAccountsRepository;
        this.contactService = contactService;
    }

    @Override
    public void store(MailAccounts mailAccounts) {
        mailAccounts.setState(MailAccountState.PENDING);
        MailAccounts ma = this.mailAccountsRepository.save(mailAccounts);
        this.activate(ma.getId());
    }

    @Override
    public void update(MailAccounts mailAccounts) {
        if (this.qMailAccountsRepository.updateByMailAccounts(mailAccounts, SecurityUtils.companyIds()) != 1) {
            throw new RuntimeException("Update failed!");
        }
    }

    @Override
    public MailAccounts getById(Long id) {
        return this.qMailAccountsRepository.getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Mail account not found."));
    }

    @Override
    public Page<MailAccounts> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qMailAccountsRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyIds());
    }

    @Override
    public void destroy(Long id) {
        this.mailAccountsRepository.delete(this.getById(id));
    }

    @Override
    public void activate(Long id) {
        MailAccounts mailAccounts = this.getById(id);
        try {
            this.getMailSender(mailAccounts.getHostName(), mailAccounts.getPort(), mailAccounts.getUserName(), mailAccounts.getPassword())
                    .testConnection();
            mailAccounts.setState(MailAccountState.ACTIVATED);
            this.qMailAccountsRepository.updateByMailAccounts(mailAccounts, SecurityUtils.companyIds());
        } catch (MessagingException e) {
            throw new RuntimeException("Mail account activating failed");
        }
    }

    @Override
    public void deactivate(Long id) {
        MailAccounts mailAccounts = this.getById(id);
        mailAccounts.setState(MailAccountState.DEACTIVATED);
        if (this.qMailAccountsRepository.updateByMailAccounts(mailAccounts, SecurityUtils.companyIds()) != 1) {
            throw new RuntimeException("Mail account deactivation failed");
        }
    }

    @Override
    public void sendEmail(String emailSubject, String emailContent, List<Long> contactIds, Long companyId) {
        try {
            JavaMailSenderImpl mailSender = this.getMailSenderByCompanyId(companyId);
            List<Contact> participants = this.contactService.getContactByIds(contactIds);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setFrom(mailSender.getUsername());
            helper.setSubject(emailSubject);
            helper.setText(emailContent, true);

            for (Contact participant: participants) {
                helper.setTo(participant.getEmail());
                mailSender.send(mimeMessage);
            }
        } catch (MessagingException e) {
            throw new RuntimeException("Sending email failed. Reason: " + e.getMessage());
        }
    }

    private JavaMailSenderImpl getMailSenderByCompanyId(Long companyId) {
        MailAccounts mailAccounts = this.getByCompanyId(companyId);
        return this.getMailSender(mailAccounts.getHostName(), mailAccounts.getPort(), mailAccounts.getUserName(), mailAccounts.getPassword());
    }

    private MailAccounts getByCompanyId(Long companyId) {
        return this.qMailAccountsRepository.getByCompaniesId(companyId)
                .orElseThrow(() -> new RuntimeException("Mail account for company not found."));
    }

    private JavaMailSenderImpl getMailSender(String hostName, int port, String userName, String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(hostName);
        mailSender.setPort(port);
        mailSender.setUsername(userName);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
