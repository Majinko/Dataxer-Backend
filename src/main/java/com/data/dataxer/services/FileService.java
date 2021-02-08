package com.data.dataxer.services;

import com.data.dataxer.models.domain.File;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    File storeFile(MultipartFile file, Boolean isDefault);

    Resource loadFileAsResource(String fileName);

    File getFileByName(String fileName, Boolean disableFilter);

    Page<File> paginate(Pageable pageable, String rqlFilter, String sortExpression, Boolean disableFilter);
}
