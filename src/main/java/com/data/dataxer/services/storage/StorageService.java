package com.data.dataxer.services.storage;

import com.data.dataxer.models.domain.Storage;

public interface StorageService {
    Storage getPreview(Long id, String type);

    Storage getById(Long id);

    void store(Storage file, Long fileAbleId, String fileAbleType);

    byte[] getFileContent(String path);

    String getUrl(String path);

    void destroy(Long id, String type);

    void destroy(Long id);
}
