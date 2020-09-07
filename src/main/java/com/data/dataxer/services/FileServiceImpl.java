package com.data.dataxer.services;

import com.data.dataxer.models.domain.File;
import com.data.dataxer.models.domain.Settings;
import com.data.dataxer.models.enums.CompanySettings;
import com.data.dataxer.repositories.FileRepository;
import com.data.dataxer.repositories.qrepositories.QFileRepository;
import com.data.dataxer.repositories.qrepositories.QSettingsRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final QFileRepository qFileRepository;
    private final QSettingsRepository qSettingsRepository;

    public FileServiceImpl(FileRepository fileRepository, QFileRepository qFileRepository, QSettingsRepository qSettingsRepository) {
        this.fileRepository = fileRepository;
        this.qFileRepository = qFileRepository;
        this.qSettingsRepository = qSettingsRepository;
    }

    @Override
    public File storeFile(MultipartFile file, Boolean isDefault, Long companyId) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = Paths.get(loadCompanyUploadDirectory()).resolve(String.valueOf(file.getResource().hashCode()));

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/downloadFile/")
                    .path(file.getName())
                    .toUriString();

            return  this.fileRepository.save(new File(file, isDefault, fileDownloadUri));
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            File file = this.qFileRepository
                    .getByName(fileName, SecurityUtils.companyIds())
                    .orElseThrow(() -> new RuntimeException("File not found " + fileName));
            Path uploadDirectory = Paths.get(loadCompanyUploadDirectory())
                    .resolve(String.valueOf(file.getFileHash()))
                    .normalize();
            Resource resource = new UrlResource(uploadDirectory.toUri());
            if (resource.exists()) {
                return resource;
            }
            throw new RuntimeException("File not found "+ fileName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found "+ fileName);
        }
    }

    @Override
    public File getFileByName(String fileName) {
        return this.qFileRepository
                .getByName(fileName, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("File not found " + fileName));
    }

    @Override
    public Page<File> paginate(Pageable pageable) {
        return this.qFileRepository.paginate(pageable, SecurityUtils.companyIds());
    }

    private String loadCompanyUploadDirectory() {
        Settings settings = this.qSettingsRepository
                .getByName(CompanySettings.FILE_UPLOAD_DIRECTORY.getName(), SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Cannot load setting "
                        + CompanySettings.FILE_UPLOAD_DIRECTORY.getName()));
        return settings.getValue();
    }

}
