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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class SettingsServiceImpl implements SettingsService{

    @Value("${file.upload-dir}")
    private String uploadDirectory;

    private final SettingsRepository settingsRepository;
    private final QSettingsRepository qSettingsRepository;
    private final CompanyRepository companyRepository;

    public SettingsServiceImpl(SettingsRepository settingsRepository, QSettingsRepository qSettingsRepository, CompanyRepository companyRepository) {
        this.settingsRepository = settingsRepository;
        this.qSettingsRepository = qSettingsRepository;
        this.companyRepository = companyRepository;
    }

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
    public Settings getByName(String name, Boolean disableFilter) {
        return this.qSettingsRepository.getByName(name, SecurityUtils.CompanyId(), disableFilter)
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
            String fileUploadDirectory = this.createUploadDirectory(
                    StringUtils.removeWhiteLetters(company.getName()).trim()
            );
            Settings settings = new Settings(
                    CompanySettings.FILE_UPLOAD_DIRECTORY.getName(),
                    fileUploadDirectory
            );
            settings.setCompany(company);
            this.settingsRepository.save(settings);
    }

    private String createUploadDirectory(String companyName) {
        Path fileStorageLocation = Paths.get(this.uploadDirectory + java.io.File.separator + companyName)
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
            return fileStorageLocation.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
}
