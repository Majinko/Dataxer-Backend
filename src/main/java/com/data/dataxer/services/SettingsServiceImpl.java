package com.data.dataxer.services;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.Settings;
import com.data.dataxer.models.enums.CompanySettings;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.SettingsRepository;
import com.data.dataxer.repositories.qrepositories.QSettingsRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService{

    private final SettingsRepository settingsRepository;
    private final QSettingsRepository qSettingsRepository;
    private final CompanyRepository companyRepository;

    public SettingsServiceImpl(SettingsRepository settingsRepository, QSettingsRepository qSettingsRepository, CompanyRepository companyRepository) {
        this.settingsRepository = settingsRepository;
        this.qSettingsRepository = qSettingsRepository;
        this.companyRepository = companyRepository;
    }

    @Value("${upload.path}")
    private String basePath;

    @Override
    public void makeSettingsForCompany(Long id) {
        Company company = this.companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company does not exists!"));
        this.createSettingsForCompany(company);
    }

    @Override
    public List<Settings> getCompanySettings(Long id) {
        return this.qSettingsRepository.getByCompanyId(id);
    }

    @Override
    public Settings getByName(String name) {
        return this.qSettingsRepository.getByName(name, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Setting does not exists!"));
    }

    @Override
    public void destroyAllCompanySettings(Long companyId) {
        this.qSettingsRepository.deleteAllSettingsByCompany(companyId);
    }

    @Override
    public void regenerateCompanySettings(Long companyId) {
        this.destroyAllCompanySettings(companyId);
        this.makeSettingsForCompany(companyId);
    }

    private void createSettingsForCompany(Company company) {
        try {
            String fileUploadDirectory = this.createUploadFileDirectory(StringUtils.removeWhiteLetters(company.getName()).trim());
            Settings settings = new Settings(
                    CompanySettings.FILE_UPLOAD_DIRECTORY.getName(),
                    fileUploadDirectory
            );
            settings.setCompany(company);
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
