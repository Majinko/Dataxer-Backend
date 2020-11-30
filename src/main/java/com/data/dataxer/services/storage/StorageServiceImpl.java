package com.data.dataxer.services.storage;

import com.data.dataxer.models.domain.Storage;
import com.data.dataxer.models.dto.StorageFileDTO;
import com.data.dataxer.repositories.StorageRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;

import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class StorageServiceImpl implements StorageService {
    private Bucket bucket;
    private final StorageRepository storageRepository;

    public StorageServiceImpl(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
        this.bucket = StorageClient.getInstance().bucket();
    }

    @Override
    public void store(StorageFileDTO file, Long fileAbleId, String fileAbleType) {
        try {
            HashMap<String, String> fileData = this.uploadFileToStorage(file);

            Storage storage = new Storage();

            storage.setFileAbleId(fileAbleId);
            storage.setFileAbleType(fileAbleType);
            storage.setExt(file.getContentType());
            storage.setSize(file.getSize());
            storage.setFileName(file.getFileName());
            storage.setIsDefault(file.getIsDefault());
            storage.setPath(fileData.get("path"));
            storage.setHashFileName(fileData.get("name"));

            this.storageRepository.save(storage);

        } catch (IOException e) {
            throw new RuntimeException("Something wrong when upload :(");
        }
    }

    @Override
    public byte[] getFile(String path) {
        return bucket.get(path).getContent();
    }

    @Override
    public String getUrl(String path) {
        return bucket.get(path).signUrl(100, TimeUnit.MINUTES).toString();
    }

    private HashMap<String, String> uploadFileToStorage(StorageFileDTO file) throws IOException {
        HashMap<String, String> fileData = new HashMap<String, String>();

        String name = generateFileName(file.getFileName());
        String path = SecurityUtils.defaultCompany().getName().toLowerCase().replaceAll("[^a-zA-Z0-9]", " ") + "/" + name;

        Blob blob = bucket.create(path, file.getContent(), file.getContentType());

        fileData.put("name", name);
        fileData.put("path", path);

        return fileData;
    }

    private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "." + getExtension(originalFileName);
    }

    private String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }
}
