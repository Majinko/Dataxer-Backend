package com.data.dataxer.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public void uploadFile(MultipartFile file);
}
