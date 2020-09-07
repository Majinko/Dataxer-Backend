package com.data.dataxer.services;

import com.data.dataxer.models.domain.File;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    File storeFile(MultipartFile file, Boolean isDefault, Long companyId);

    Resource loadFileAsResource(String fileName);

    File getFileByName(String fileName);

    Page<File> paginate(Pageable pageable);
}
