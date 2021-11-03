package com.data.dataxer.services;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.domain.MailAccounts;
import com.data.dataxer.models.domain.MailTemplate;
import com.data.dataxer.models.enums.MailAccountState;
import com.data.dataxer.repositories.MailAccountsRepository;
import com.data.dataxer.repositories.qrepositories.QMailAccountsRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.core.env.Environment;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressCriteria;
import org.hazlewood.connor.bottema.emailaddress.EmailAddressValidator;
import org.simplejavamail.api.email.AttachmentResource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.Recipient;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class MailAccountsServiceImpl implements MailAccountsService {

    private final MailAccountsRepository mailAccountsRepository;
    private final QMailAccountsRepository qMailAccountsRepository;
    private final ContactService contactService;
    private final MailTemplatesService mailTemplatesService;
    private final Environment environment;
    private final FileService fileService;

    public MailAccountsServiceImpl(MailAccountsRepository mailAccountsRepository, QMailAccountsRepository qMailAccountsRepository,
                                   ContactService contactService, MailTemplatesService mailTemplatesService,
                                   Environment environment, FileService fileService) {
        this.mailAccountsRepository = mailAccountsRepository;
        this.qMailAccountsRepository = qMailAccountsRepository;
        this.contactService = contactService;
        this.mailTemplatesService = mailTemplatesService;
        this.environment = environment;
        this.fileService = fileService;
    }

    @Override
    @Transactional
    public void store(MailAccounts mailAccounts) {
        mailAccounts.setState(MailAccountState.PENDING);
        MailAccounts ma = this.mailAccountsRepository.save(mailAccounts);
        this.activate(ma.getId());
    }

    @Override
    @Transactional
    public void update(MailAccounts mailAccounts) {
        if (this.qMailAccountsRepository.updateByMailAccounts(mailAccounts, SecurityUtils.companyId()) != 1) {
            throw new RuntimeException("Update failed!");
        }
    }

    @Override
    public MailAccounts getById(Long id) {
        return this.qMailAccountsRepository.getById(id, SecurityUtils.companyId())
                .orElseThrow(() -> new RuntimeException("Mail account not found."));
    }

    @Override
    public Page<MailAccounts> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qMailAccountsRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyId());
    }

    @Override
    public void destroy(Long id) {
        this.mailAccountsRepository.delete(this.getById(id));
    }

    @Override
    @Transactional
    public void activate(Long id) {
        MailAccounts mailAccounts = this.getById(id);
        try {
            this.getMailSender(mailAccounts)
                    .testConnection();
            mailAccounts.setState(MailAccountState.ACTIVATED);
            this.qMailAccountsRepository.updateByMailAccounts(mailAccounts, SecurityUtils.companyId());
        } catch (MessagingException e) {
            throw new RuntimeException("Mail account activating failed");
        }
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        MailAccounts mailAccounts = this.getById(id);
        mailAccounts.setState(MailAccountState.DEACTIVATED);
        if (this.qMailAccountsRepository.updateByMailAccounts(mailAccounts, SecurityUtils.companyId()) != 1) {
            throw new RuntimeException("Mail account deactivation failed");
        }
    }

    @Override
    public void sendEmail(String emailSubject, String emailContent, List<Long> contactIds, Long companyId, Long templateId) {
        try {
            JavaMailSenderImpl mailSender = this.getMailSenderByCompanyId(companyId);
            List<Contact> participants = this.contactService.getContactByIds(contactIds);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);

            helper.setFrom(mailSender.getUsername());

            if (emailSubject != null && emailContent != null && !emailSubject.isEmpty() && !emailContent.isEmpty() ) {
                helper.setSubject(emailSubject);
                helper.setText(emailContent, true);
            } else if (templateId != null) {
                MailTemplate mailTemplates = this.mailTemplatesService.getById(templateId);
                helper.setSubject(mailTemplates.getEmailSubject());
                helper.setText(mailTemplates.getEmailContent(), true);
            } else {
                throw new RuntimeException("Missing required mail subject and email content, or template id");
            }

            for (Contact participant: participants) {
                helper.setTo(participant.getEmail());
                mailSender.send(mimeMessage);
            }
        } catch (MessagingException e) {
            throw new RuntimeException("Sending email failed. Reason: " + e.getMessage());
        }
    }

    @Override
    public void sendEmailWithAttachments(String subject, String content, List<Long> recipientIds, Long templateId, List<String> fileNames, Long companyId) {
        companyId = companyId != null ? companyId : SecurityUtils.companyId();

        MailAccounts mailAccounts = this.getByCompanyId(companyId);
        if (templateId != null) {
            MailTemplate mailTemplates = this.mailTemplatesService.getById(templateId);
            subject = mailTemplates.getEmailSubject();
            content = mailTemplates.getEmailContent();
        }

        String host = mailAccounts != null ? mailAccounts.getHostName() : environment.getProperty("spring.mail.host");
        int port = mailAccounts != null ? mailAccounts.getPort() : Integer.parseInt(environment.getProperty("spring.mail.port"));
        String username = mailAccounts != null ? mailAccounts.getUserName() : environment.getProperty("spring.mail.username");
        String password = mailAccounts != null ? mailAccounts.getPassword() : environment.getProperty("spring.mail.password");

        Email email = createEmail(username, subject, content, recipientIds, fileNames);

        MailerBuilder.withSMTPServer(host, port, username, password)
                .buildMailer()
                .sendMail(email);
    }

    private Email createEmail(String from, String subject, String body, List<Long> recipientIds, List<String> fileNames) {
        return EmailBuilder.startingBlank()
                .from(from)
                .to(this.makeRecipients(recipientIds))
                .withSubject(subject)
                .withHTMLText(body)
                .withEmbeddedImageAutoResolutionForFiles(true)
                .withAttachments(makeAttachments(fileNames))
                .buildEmail();
    }

    private List<AttachmentResource> makeAttachments(List<String> fileNames) {
        List<AttachmentResource> attachments = new ArrayList<>();

        if (fileNames == null) {
            return attachments;
        }

        fileNames.forEach(fileName-> {

            try {
                Resource resource = fileService.loadFileAsResource(fileName);
                FileDataSource dataSource = new FileDataSource(resource.getFile());
                AttachmentResource attachment = new AttachmentResource(fileName, dataSource);

                attachments.add(attachment);
            } catch (IOException exception) {
                throw new RuntimeException("File is broken");
            }
        });

        return attachments;
    }

    private List<Recipient> makeRecipients(List<Long> recipientIds) {
        List<Recipient> recipients = new ArrayList<>();

        List<Contact> participants = this.contactService.getContactByIds(recipientIds);
        participants.forEach(participant-> {
            if (EmailAddressValidator.isValid(participant.getEmail(), EmailAddressCriteria.RFC_COMPLIANT)) {
                Recipient recipient = new Recipient(participant.getName(), participant.getEmail(), Message.RecipientType.TO);
                recipients.add(recipient);
            }
        });

        return recipients;
    }

    private JavaMailSenderImpl getMailSenderByCompanyId(Long companyId) {
        MailAccounts mailAccounts = this.getByCompanyId(companyId);
        return this.getMailSender(mailAccounts);
    }

    private MailAccounts getByCompanyId(Long companyId) {
        return this.qMailAccountsRepository.getByCompaniesId(companyId)
                .orElse(null);
    }

    private JavaMailSenderImpl getMailSender(MailAccounts mailAccounts) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        if (mailAccounts != null) {
            mailSender.setHost(mailAccounts.getHostName());
            mailSender.setPort(mailAccounts.getPort());
            mailSender.setUsername(mailAccounts.getUserName());
            mailSender.setPassword(mailAccounts.getPassword());
        } else {
            mailSender.setHost(environment.getProperty("spring.mail.host"));
            mailSender.setPort(Integer.parseInt(environment.getProperty("spring.mail.port")));
            mailSender.setUsername(environment.getProperty("spring.mail.username"));
            mailSender.setPassword(environment.getProperty("spring.mail.password"));
        }

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return mailSender;
    }
}
