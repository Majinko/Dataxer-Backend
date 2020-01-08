package com.data.dataxer.services;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

public class FileServiceImpl implements FileService
{
    /*@Value("${app.upload.dir:${user.home}}")*/
    public String uploadDir;

    @Override
    public void uploadFile(MultipartFile file) {

    }
}
