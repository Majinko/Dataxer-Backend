package com.data.dataxer.services;

import com.data.dataxer.models.domain.MailAccounts;
import com.data.dataxer.models.enums.MailAccountState;
import com.data.dataxer.repositories.MailAccountsRepository;
import com.data.dataxer.repositories.qrepositories.QMailAccountsRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@Service
public class MailAccountsServiceImpl implements MailAccountsService {

    /*@Autowired
    private Environment env;*/

    private final MailAccountsRepository mailAccountsRepository;
    private final QMailAccountsRepository qMailAccountsRepository;
    private final Environment environment;
    private final ContactService contactService;
    private final MailTemplatesService mailTemplatesService;

    public MailAccountsServiceImpl(MailAccountsRepository mailAccountsRepository, QMailAccountsRepository qMailAccountsRepository,
                                   ContactService contactService, MailTemplatesService mailTemplatesService, Environment environment) {
        this.mailAccountsRepository = mailAccountsRepository;
        this.qMailAccountsRepository = qMailAccountsRepository;
        this.contactService = contactService;
        this.mailTemplatesService = mailTemplatesService;
        this.environment = environment;
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
    public void sendEmail(String emailSubject, String emailContent, List<String> emails) {
        try {
            JavaMailSenderImpl mailSender = this.getMailSenderByCompanyId(SecurityUtils.companyId());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);

            helper.setFrom(mailSender.getUsername());
            helper.setSubject(emailSubject);
            helper.setText(emailContent, true);

            for (String email : emails) {
                helper.setTo(email);
                mailSender.send(mimeMessage);
            }
        } catch (MessagingException e) {
            throw new RuntimeException("Sending email failed. Reason: " + e.getMessage());
        }
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

    /*private JavaMailSenderImpl getMailSender(String hostName, int port, String userName, String password) {
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
    }*/
}
