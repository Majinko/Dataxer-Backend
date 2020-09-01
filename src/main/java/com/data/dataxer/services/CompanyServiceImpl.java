package com.data.dataxer.services;
import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.Settings;
import com.data.dataxer.models.enums.CompanySettings;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.AppUserRepository;
import com.data.dataxer.repositories.SettingsRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Value("${upload.path}")
    private String basePath;

    private final CompanyRepository companyRepository;
    private final AppUserRepository appUserRepository;
    private final SettingsRepository settingsRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, AppUserRepository appUserRepository, SettingsRepository settingsRepository) {
        this.companyRepository = companyRepository;
        this.appUserRepository = appUserRepository;
        this.settingsRepository = settingsRepository;
    }

    @Override
    @Transactional
    public Company store(Company company) {
        AppUser appUser = appUserRepository
                .findById(SecurityUtils.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        company.setAppUser(appUser);
        this.companySetBi(company);

        Company c = this.companyRepository.save(company);

        appUser.getCompanies().add(c);
        appUserRepository.save(appUser);

        return c;
    }

    public void companySetBi(Company company) {
        company.getBillingInformation().forEach(billingInformation -> {
            billingInformation.setCompany(company);
        });
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.findByAppUserId(SecurityUtils.id());
    }

    @Override
    public Company findById(Long id) {
        return companyRepository.findAllByIdAndAppUserId(id, SecurityUtils.id()).orElse(null);
    }

    @Override
    @Transactional
    public Company update(Company company, Long id) {
        Company c = this.findById(id);

        c.setName(company.getName());
        c.setLegalForm(company.getLegalForm());
        c.setStreet(company.getStreet());
        c.setCity(company.getCity());
        c.setPostalCode(company.getPostalCode());
        c.setCountry(company.getCountry());
        c.setEmail(company.getEmail());
        c.setPhone(company.getPhone());
        c.setWeb(company.getWeb());
        c.setIdentifyingNumber(company.getIdentifyingNumber());
        c.setVat(company.getVat());
        c.setNetOfVat(company.getNetOfVat());
        c.setIban(company.getIban());

        // company set bi
        c.getBillingInformation().clear();
        c.getBillingInformation().addAll(company.getBillingInformation());
        companySetBi(c);

        companyRepository.save(c);

        return c;
    }

    @Override
    @Transactional
    public void createSettingsForCompany(Company company) {
        try {
            String fileUploadDirectory = this.createUploadFileDirectory(StringUtils.removeWhiteLetters(company.getName()).trim());
            Settings settings = new Settings(
                    CompanySettings.FILE_UPLOAD_DIRECTORY.getName(),
                    fileUploadDirectory
            );
            this.settingsRepository.save(settings);
        } catch (IOException e) {
            throw new RuntimeException("Cannot create settings for company: " + company.getName() + e.getMessage());
        }
    }

    private String createUploadFileDirectory(String companyName) throws IOException {
        String uploadDir = this.basePath + File.separator + companyName + File.separator;
        if(!Files.exists(Paths.get(uploadDir))) {
            Files.createDirectories(Paths.get(uploadDir), PosixFilePermissions.asFileAttribute(
                    PosixFilePermissions.fromString("rwxrwxrwx")));
        }
        return uploadDir;
    }
}
