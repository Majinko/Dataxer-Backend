package com.data.dataxer.services.storage;

import com.data.dataxer.models.dto.StorageFileDTO;

public interface StorageService {
    void store(StorageFileDTO file, Long fileAbleId, String fileAbleType);
    byte[] getFile(String path);
    String getUrl(String path);
}
